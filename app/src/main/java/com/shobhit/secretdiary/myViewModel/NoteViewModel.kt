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
class NoteViewModel(
    context: Context,
    private var repository: NoteRepository
) : ViewModel() {

    // Get the logged-in user's email from the session
    val email: String = SessionManager(context).getUserEmail() ?: ""

    // Job reference for delayed save operations (to prevent rapid multiple saves)
    private var saveJob: Job? = null

    // Current note ID being edited or created (0 means new note)
    var currentNoteId: Int = 0

    /**
     * Retrieves all notes for the current logged-in user.
     * @return LiveData list of NoteEntity objects.
     */
    fun getNotes(): LiveData<List<NoteEntity>> {
        return repository.getNotesByEmail(email)
    }

    /**
     * Inserts a new note or updates an existing one after a short delay (500ms).
     * This delay helps in reducing unnecessary database writes during rapid typing.
     * @param note The NoteEntity object to be saved.
     */
    fun insert(note: NoteEntity) {
        saveJob?.cancel() // Cancel any ongoing save job to avoid duplicates
        saveJob = viewModelScope.launch {
            delay(500) // Delay to prevent excessive writes
            val newId = repository.insert(note.copy(id = currentNoteId))

            // If creating a new note, update currentNoteId with the generated ID
            if (currentNoteId == 0) {
                currentNoteId = newId.toInt()
            }
        }
    }

    /**
     * Searches notes based on a query string for the logged-in user.
     * @param query The search keyword.
     * @return LiveData list of matching NoteEntity objects.
     */
    fun searchNotes(query: String): LiveData<List<NoteEntity>> {
        return repository.searchNotes(email, query)
    }

    /**
     * Deletes a note by its ID.
     * @param id The note's unique ID.
     */
    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
    }

    /**
     * Sets the current note ID (used when editing existing notes).
     * @param id The note's unique ID.
     */
    fun setNoteId(id: Int) {
        currentNoteId = id
    }

    /**
     * Retrieves a note by its ID.
     * @param id The note's unique ID.
     * @return LiveData of NoteEntity.
     */
    fun getNoteById(id: Int): LiveData<NoteEntity> {
        return repository.getNoteById(id)
    }
}
