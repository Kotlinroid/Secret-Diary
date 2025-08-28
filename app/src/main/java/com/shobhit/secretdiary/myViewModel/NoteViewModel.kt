package com.shobhit.secretdiary.myViewModel

import android.content.Context
import androidx.lifecycle.*
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myUtilities.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoteViewModel(context: Context, private var repository: NoteRepository) : ViewModel() {

    val email = SessionManager(context).getUserEmail() ?: ""
    private var saveJob: Job? = null
    var currentNoteId: Int = 0

    fun getNotes(): LiveData<List<NoteEntity>> {
        return repository.getNotesByEmail(email)
    }

    fun insert(note: NoteEntity) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500)
            val newId = repository.insert(note.copy(id = currentNoteId))

            if (currentNoteId == 0) {
                currentNoteId = newId.toInt()
            }
        }
    }

    fun searchNotes( query: String): LiveData<List<NoteEntity>> {
        return repository.searchNotes(email, query)
    }

    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
    }
    fun setNoteId(id: Int) {
        currentNoteId = id
    }

    fun getNoteById(id: Int): LiveData<NoteEntity> {
            return repository.getNoteById(id)
    }
}
