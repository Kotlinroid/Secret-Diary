package com.shobhit.secretdiary.myInterface

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shobhit.secretdiary.myDataClass.NoteEntity

/**
 * Data Access Object (DAO) for managing notes in the Room database.
 * Handles inserting, searching, retrieving, and deleting notes.
 */
@Dao
interface NoteDao {

    /**
     * Inserts a note into the database.
     * If a note with the same ID exists, it will be replaced.
     * Returns the ID of the inserted or updated note.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    /**
     * Searches notes for the given user.
     * Looks for matches in the title or content using the LIKE operator.
     * Returns the results as LiveData so the UI updates automatically.
     */
    @Query(
        """
        SELECT * FROM notes
        WHERE email = :email 
        AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY id DESC
        """
    )
    fun searchNotes(email: String, query: String): LiveData<List<NoteEntity>>

    /**
     * Deletes the note with the specified ID from the database.
     */
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Retrieves all notes for the given user, ordered from newest to oldest.
     * Returns a LiveData list so updates are observed in real-time.
     */
    @Query("SELECT * FROM notes WHERE email = :email ORDER BY id DESC")
    fun getNotesByEmail(email: String): LiveData<List<NoteEntity>>

    /**
     * Retrieves a single note by its ID.
     * Returns the note as LiveData for real-time observation.
     */
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Int): LiveData<NoteEntity>
}
