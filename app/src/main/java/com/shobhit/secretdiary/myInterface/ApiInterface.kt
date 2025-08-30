package com.shobhit.secretdiary.myInterface

import com.shobhit.secretdiary.myDataClass.LoginRequest
import com.shobhit.secretdiary.myDataClass.LoginResponse
import com.shobhit.secretdiary.myDataClass.RegisterRequest
import com.shobhit.secretdiary.myDataClass.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("third/account/login/")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("third/account/register/")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("third/account/logout/")
    suspend fun logoutUser(@Header("Authorization") token: String): Response<Unit>
}