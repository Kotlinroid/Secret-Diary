package com.shobhit.secretdiary.myViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobhit.secretdiary.myDataClass.LoginRequest
import com.shobhit.secretdiary.myDataClass.LoginResponse
import com.shobhit.secretdiary.myDataClass.RegisterRequest
import com.shobhit.secretdiary.myDataClass.RegisterResponse
import com.shobhit.secretdiary.myRepository.AuthRepository
import com.shobhit.secretdiary.myUtilities.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * ViewModel for authentication-related actions such as login and registration.
 * It interacts with the AuthRepository to fetch and send authentication data.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // MutableLiveData for internal updates, exposed as immutable LiveData
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    private val _loginResponse = MutableLiveData<LoginResponse>()
    private val _errorMessage = MutableLiveData<String>()
    private val _buttonStatus = MutableLiveData<Boolean>()

    // Public LiveData for UI observation
    val registerResponse: LiveData<RegisterResponse> = _registerResponse
    val loginResponse: LiveData<LoginResponse> = _loginResponse
    val errorMessage: LiveData<String> = _errorMessage
    val buttonStatus: LiveData<Boolean> = _buttonStatus

    /**
     * Logs in the user by sending a LoginRequest to the repository.
     * Updates UI state accordingly.
     */
    fun loginUser(loginRequest: LoginRequest) {
        _buttonStatus.value = false // Disable button during API call
        viewModelScope.launch {
            try {
                val response = authRepository.loginUser(loginRequest)
                if (response.isSuccessful) {
                    // Save user data and update login response
                    _loginResponse.value = response.body()
                    sessionManager.saveUser(response.body()!!, loginRequest.email)
                    _buttonStatus.value = true
                } else {
                    // Parse error message from server response
                    val errorJson = response.errorBody()?.string()
                    val errorMsg = try {
                        JSONObject(errorJson ?: "").optString("detail", "Login failed")
                    } catch (e: Exception) {
                        "Login failed"
                    }
                    _errorMessage.value = errorMsg
                    _buttonStatus.value = true
                }
            } catch (e: Exception) {
                // Handle unexpected exceptions
                _errorMessage.value = "Exception: ${e.message}"
                _buttonStatus.value = true
            }
        }
    }

    /**
     * Registers a new user by sending a RegisterRequest to the repository.
     * Updates UI state accordingly.
     */
    fun registerUser(registerRequest: RegisterRequest) {
        _buttonStatus.value = false // Disable button during API call
        viewModelScope.launch {
            try {
                val response = authRepository.registerUser(registerRequest)
                if (response.isSuccessful) {
                    // Update register response
                    _registerResponse.value = response.body()
                    _buttonStatus.value = true
                } else {
                    // Parse error message from server response
                    val errorJson = response.errorBody()?.string()
                    val errorMsg = try {
                        JSONObject(errorJson ?: "").optString("detail", "Registration failed")
                    } catch (e: Exception) {
                        "Registration failed"
                    }
                    _errorMessage.value = errorMsg
                    _buttonStatus.value = true
                }
            } catch (e: Exception) {
                // Handle unexpected exceptions
                _errorMessage.value = "Exception: ${e.message}"
                _buttonStatus.value = true
            }
        }
    }

    /**
     * Logs out the user by sending a token to the repository.
     * Updates UI state accordingly.
     */
    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logoutUser(sessionManager.getUserAccessToken())
            sessionManager.logout()
        }
    }
}
