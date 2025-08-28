package com.shobhit.secretdiary.myViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shobhit.secretdiary.myRepository.AuthRepository
import com.shobhit.secretdiary.myUtilities.SessionManager
import com.shobhit.secretdiary.myViewModel.AuthViewModel

class AuthViewModelFactory(private val authRepository: AuthRepository, private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}