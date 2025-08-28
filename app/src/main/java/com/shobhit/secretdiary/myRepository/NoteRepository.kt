package com.shobhit.secretdiary.myRepository

import androidx.lifecycle.LiveData
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myInterface.NoteDao

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotesByEmail(email: String): LiveData<List<NoteEntity>> {
        return noteDao.getNotesByEmail(email)
    }

    suspend fun insert(note: NoteEntity): Long {
        return noteDao.insert(note)
    }

    suspend fun deleteById(id: Int) {
        noteDao.deleteById(id)
    }

    fun searchNotes(email: String, query: String): LiveData<List<NoteEntity>> {
        return noteDao.searchNotes(email, query)
    }

    fun getNoteById(id: Int): LiveData<NoteEntity> {
        return noteDao.getNoteById(id)
    }
}

