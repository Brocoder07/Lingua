package com.deploy.language_app

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deploy.language_app.api.BackendApi
import com.deploy.language_app.api.RetrofitClient
import com.deploy.language_app.api.User
import com.google.firebase.auth.FirebaseAuth
import retrofit2.HttpException
import java.time.Instant
import java.time.format.DateTimeFormatter

//ViewModel class to manage authentication state using Firebase Authentication
class AuthViewModel : ViewModel() {
    //FirebaseAuth instance to handle authentication operations
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //LiveData to expose the current authentication state
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val apiInstance: BackendApi = RetrofitClient.instance

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
    //Function to validate that only specific email domains can log in or sign up
    private fun isValidEmailDomain(email: String): Boolean {
        //Check if the email ends with the specified domain
        return email.endsWith("@gmail.com", ignoreCase = true)
    }

    private fun postUserData(firebaseUid: String, email: String, languages: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val time = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                apiInstance.registerUser(
                    User(
                        firebase_uid = firebaseUid,
                        email = email,
                        languages = languages,
                        created_at = time,
                        is_active = true
                    )
                )
                Log.d("AuthViewModel", "User data sent successfully")
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Error sending user data: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected: ${e.message}")
            }
        }
    }

    private fun getUserData(firebaseUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val returnedUser = apiInstance.getUserProfile(
                    firebase_uid = firebaseUid
                )
                Log.d("AuthViewModel", "User data got successfully: ")
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Error sending user data: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected: ${e.message}")
            }
        }
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
            _authState.value = AuthState.Error("Please enter a valid Email address")
            return
        }
        //Set state to loading while trying to sign in
        _authState.value = AuthState.Loading
        //Firebase sign-in operation
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                //If sign-in is successful, set state to authenticated
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    //If sign-in fails, set an error message
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
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
            _authState.value = AuthState.Error("Please enter a valid Email address")
            return
        }
        //Set state to loading while trying to sign up
        _authState.value = AuthState.Loading
        //Firebase sign-up operation
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                //If sign-up is successful, set state to authenticated
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val currentUserFirebaseId = currentUser.uid
                        val currentUserEmail = currentUser.email
                        val list = listOf<String>()
                        if (currentUserEmail != null) {
                            postUserData(currentUserFirebaseId, currentUserEmail, list)
                        }
                    }
                } else {
                    //If sign-up fails, set an error message
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }
    //Function to sign out the user
    fun signOut() {
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