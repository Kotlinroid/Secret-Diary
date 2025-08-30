package com.shobhit.secretdiary.myRepository

import com.shobhit.secretdiary.myDataClass.LoginRequest
import com.shobhit.secretdiary.myDataClass.LoginResponse
import com.shobhit.secretdiary.myDataClass.RegisterRequest
import com.shobhit.secretdiary.myDataClass.RegisterResponse
import com.shobhit.secretdiary.myInterface.ApiInterface
import retrofit2.Response

class AuthRepository(private val apiService: ApiInterface) {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse> {
        return apiService.registerUser(registerRequest)
    }

    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse> {
        return apiService.loginUser(loginRequest)
    }

    suspend fun logoutUser(token: String?): Response<Unit> {
        return apiService.logoutUser("Bearer $token")
    }
}