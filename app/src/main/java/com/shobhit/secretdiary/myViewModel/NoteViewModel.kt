package com.shobhit.secretdiary.myViewModel

import android.content.Context
import androidx.lifecycle.*
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myUtilities.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing Note data between UI and Repository.
 * Uses LiveData to observe data changes and Coroutines for background operations.
 */
class NoteViewModel(context: Context, private var repository: NoteRepository) : ViewModel() {


    // Get the logged-in user's email from the session
    val email: String = SessionManager(context).getUserEmail() ?: ""

    // Job reference for delayed save operations (to prevent rapid multiple saves)
    private var saveJob: Job? = null

    // Current note ID being edited or created (0 means new note)
    var currentNoteId: Int = 0

    private val _save = MutableLiveData<String>()
    val save: LiveData<String> = _save

    /**
     * Retrieves all notes for the current logged-in user.
     */
    fun getNotes(): LiveData<List<NoteEntity>> {
        return repository.getNotesByEmail(email)
    }

    /**
     * Inserts a new note or updates an existing one after a short delay (500ms).
     * This delay helps in reducing unnecessary database writes during rapid typing.
     */
    fun insert(note: NoteEntity) {
        saveJob?.cancel() // Cancel any ongoing save job to avoid duplicates
        _save.value = "Saving..."

        saveJob = viewModelScope.launch {
            delay(500) // Delay to prevent excessive writes
            val newId = repository.insert(note.copy(id = currentNoteId))
            _save.value = "Saved"

            // If creating a new note, update currentNoteId with the generated ID
            if (currentNoteId == 0) {
                currentNoteId = newId.toInt()
            }
        }
    }

    /**
     * Searches notes based on a query string for the logged-in user.
     */
    fun searchNotes(query: String): LiveData<List<NoteEntity>> {
        return repository.searchNotes(email, query)
    }

    /**
     * Deletes a note by its ID.
     */
    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
    }

    /**
     * Sets the current note ID (used when editing existing notes).
     */
    fun setNoteId(id: Int) {
        currentNoteId = id
    }

    /**
     * Retrieves a note by its ID.
     */
    fun getNoteById(id: Int): LiveData<NoteEntity> {
        return repository.getNoteById(id)
    }
}
