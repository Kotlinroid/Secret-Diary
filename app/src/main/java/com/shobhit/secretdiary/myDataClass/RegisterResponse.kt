package com.shobhit.secretdiary.myDataClass

data class RegisterResponse(
    val id: Int,
    val email: String,
    val username: String,
    val first_name: String,
    val last_name: String,
    val role: String
)
