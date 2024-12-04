package com.deploy.language_app

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deploy.language_app.api.BackendApi
import com.deploy.language_app.api.RetrofitClient
import com.deploy.language_app.api.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.Instant
import java.time.format.DateTimeFormatter

class AuthViewModelFactory(private val backendApi: BackendApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(backendApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
//ViewModel class to manage authentication state using Firebase Authentication
class AuthViewModel(backendApi1: BackendApi) : ViewModel() {
    //FirebaseAuth instance to handle authentication operations
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val backendApi: BackendApi = RetrofitClient.instance
    //LiveData to expose the current authentication state
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile
    private val firebaseuid = auth.currentUser?.uid
    //Initialization block to check the authentication status when the ViewModel is created
    init {
        checkAuthStatus() //Check if the user is already logged in
    }
    //Function to check if the user is authenticated when the app starts
    private fun checkAuthStatus() {
        //If there's no current user, set state as unauthenticated; otherwise, authenticated
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }
    private fun fetchUserProfile() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                try {
                    val profile = backendApi.getUserProfile(uid)
                    _userProfile.postValue(profile)
                } catch (e: Exception) {
                    _authState.postValue(AuthState.Error("Failed to fetch user profile"))
                }
            }
        }
    }
    fun updateUserProfile(userUpdate: Map<String, Any>) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                try {
                    val updatedProfile = backendApi.updateUserProfile(uid, userUpdate)
                    _userProfile.postValue(updatedProfile)
                } catch (e: Exception) {
                    _authState.postValue(AuthState.Error("Failed to update user profile"))
                }
            }
        }
    }
    //Function to validate that only specific email domains can log in or sign up
    private fun isValidEmailDomain(email: String): Boolean {
        //Check if the email ends with the specified domain
        return email.endsWith("@gmail.com", ignoreCase = true)
    }
    //Function to handle user login with email and password
    fun login(email: String, password: String) {
        //Check if the email or password is empty, and show error if true
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password cannot be empty")
            return
        }
        //Validate if the email belongs to the correct domain
        if (!isValidEmailDomain(email)) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }
        //Set state to loading while trying to sign in
        _authState.value = AuthState.Loading
        //Firebase sign-in operation
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                //If sign-in is successful, set state to authenticated
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        storeUserInBackend(currentUser.uid, currentUser.email ?: "")
                    }
                    _authState.value = AuthState.Authenticated
                    fetchUserProfile()
                } else {
                    //If sign-in fails, set an error message
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
        Log.i(TAG,"firebaseid: ${firebaseuid}")
    }
    private fun storeUserInBackend(firebaseUid: String, email: String) {
        viewModelScope.launch {
            try {
                backendApi.registerUser(User(firebase_uid = firebaseUid, email = email))
                Log.d("AuthViewModel", "User stored in backend successfully")
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Failed to store user: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error: ${e.message}")
            }
        }
    }
    //Function to handle user sign-up with email and password
    fun signup(email: String, password: String) {
        //Check if email or password is empty, and show error if true
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password cannot be empty")
            return
        }
        //Validate if the email belongs to the correct domain
        if (!isValidEmailDomain(email)) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }
        //Set state to loading while trying to sign up
        _authState.value = AuthState.Loading
        //Firebase sign-up operation
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                //If sign-up is successful, set state to authenticated
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        storeUserInBackend(currentUser.uid, currentUser.email ?: "")
                    }
                    _authState.value = AuthState.Authenticated
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    viewModelScope.launch {
                        try {
                            val current_time = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                            val userProfile = UserProfile(
                                firebase_uid = uid,
                                email = email,
                                languages = listOf(), //Empty list for languages
                                current_time = current_time //Assigning the current time directly as a string
                            )
                            backendApi.updateUserProfile(uid, userProfile)
                            fetchUserProfile()
                        } catch (e: Exception) {
                            _authState.postValue(AuthState.Error("Failed to sync user profile with backend"))
                        }
                    }
                } else {
                    //If sign-up fails, set an error message
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }
    //Function to sign out the user
    fun signout() {
        //Firebase sign-out operation
        auth.signOut()
        //Set state to unauthenticated after sign-out
        _authState.value = AuthState.Unauthenticated
    }
}
//Sealed class representing different authentication states
sealed class AuthState {
    //Represents an authenticated state
    data object Authenticated : AuthState()
    //Represents an unauthenticated state
    data object Unauthenticated : AuthState()
    //Represents a loading state during authentication actions
    data object Loading : AuthState()
    //Represents an error state with an error message
    data class Error(val message: String) : AuthState()
}
