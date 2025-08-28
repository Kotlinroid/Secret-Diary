package com.shobhit.secretdiary.myInterface

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shobhit.secretdiary.myDataClass.NoteEntity

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Query("""SELECT * FROM notes WHERE email = :email AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY id DESC""")
    fun searchNotes(email: String, query: String): LiveData<List<NoteEntity>>
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM notes WHERE email = :email ORDER BY id DESC")
    fun getNotesByEmail(email: String): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Int): LiveData<NoteEntity>

}
