package com.shobhit.secretdiary.myDataClass

data class RegisterRequest(
    val email: String,
    val username: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val password_confirm: String,
    val role: String
)
