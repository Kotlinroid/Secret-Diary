package com.shobhit.secretdiary.myUtilities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myInterface.NoteDao

/**
 * Room database class for storing and managing notes.
 * This database provides access to the NoteDao for performing CRUD operations.
 */
@Database(entities = [NoteEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    // DAO to perform operations on the notes table
    abstract fun noteDao(): NoteDao

    companion object {
        // Ensures only one instance of the database exists across the app
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        /**
         * Returns the singleton instance of the NoteDatabase.
         * If it doesn't exist, creates a new instance.
         * Uses synchronized block to make it thread-safe.
         */
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database" // Database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
