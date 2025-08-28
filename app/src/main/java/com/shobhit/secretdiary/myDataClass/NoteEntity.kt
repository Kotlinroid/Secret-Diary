package com.shobhit.secretdiary.myDataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String?,
    val date: String?,
    val title: String,
    val content: String,
    val isLocked: Boolean = false
)