package com.shobhit.secretdiary.myViewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myViewModel.NoteViewModel

class NoteViewModelFactory(private val context: Context, private val repository: NoteRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}