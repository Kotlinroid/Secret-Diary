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

class AuthViewModel(private val authRepository: AuthRepository, private val sessionManager: SessionManager): ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    private val _loginResponse = MutableLiveData<LoginResponse>()
    private val _errorMessage = MutableLiveData<String>()
    private val _buttonStatus = MutableLiveData<Boolean>()

    val registerResponse: LiveData<RegisterResponse> = _registerResponse
    val loginResponse: LiveData<LoginResponse> = _loginResponse
    val errorMessage: LiveData<String> = _errorMessage
    val buttonStatus: LiveData<Boolean> = _buttonStatus

    fun loginUser(loginRequest: LoginRequest){
        _buttonStatus.value = false
        viewModelScope.launch {
            try {
                val response = authRepository.loginUser(loginRequest)
                if(response.isSuccessful) {
                    _loginResponse.value = response.body()
                    sessionManager.saveUser(response.body()!!, loginRequest.email)
                    _buttonStatus.value = true
                } else {
                    val errorJson = response.errorBody()!!.string()
                    val errorMessage = try {
                        JSONObject(errorJson).optString("detail", "Login failed")
                    } catch (e: Exception) {
                        "Login failed"
                    }
                    _errorMessage.value = errorMessage
                    _buttonStatus.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                _buttonStatus.value = true
            }
        }
    }

    fun registerUser(registerRequest: RegisterRequest){
        _buttonStatus.value = false
        viewModelScope.launch {
            try {
                val response = authRepository.registerUser(registerRequest)
                if (response.isSuccessful){
                    _registerResponse.value = response.body()
                    _buttonStatus.value = true
                } else {
                    val errorJson = response.errorBody()!!.string()
                    val errorMessage = try {
                        JSONObject(errorJson).optString("detail", "Login failed")
                    } catch (e: Exception) {
                        "Login failed"
                    }
                    _errorMessage.value = errorMessage
                    _buttonStatus.value = true
                }
            } catch (e: Exception){
                _errorMessage.value = "Exception: ${e.message}"
                _buttonStatus.value = true
            }
        }
    }
}