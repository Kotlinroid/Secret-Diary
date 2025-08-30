package com.shobhit.secretdiary.myActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.shobhit.secretdiary.databinding.ActivityNewNoteBinding
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myObject.CallInterface
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myUtilities.NoteDatabase
import com.shobhit.secretdiary.myViewModel.NoteViewModel
import com.shobhit.secretdiary.myViewModelFactory.NoteViewModelFactory

/**
 * Activity for creating or editing a note.
 * Handles note saving, deletion, and loading existing note data.
 */
class NewNoteActivity : AppCompatActivity() {
    // DataBinding for accessing UI components
    private lateinit var binding: ActivityNewNoteBinding

    // ViewModel to manage note-related data
    private lateinit var noteViewModel: NoteViewModel

    // Email of the logged-in user
    private lateinit var email: String

    // Current date string for note creation
    private lateinit var currentDate: String
    private var isLocked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)

        // Disable night mode for this activity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        /** ----------------- ViewModel Initialization ----------------- **/
        val dao = NoteDatabase.getDatabase(this).noteDao()
        val noteRepository = NoteRepository(dao)
        val factory = NoteViewModelFactory(this, noteRepository)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        /** ----------------- Back Button Click ----------------- **/
        binding.backButtonLayout.setOnClickListener {
            saveNote()
            CallInterface.onClickListener.onClickListener("Note Saved.")
            finish()
        }

        /** ----------------- Get Intent Data ----------------- **/
        val id = intent.getIntExtra("id", 0)
        email = intent.getStringExtra("email").toString()
        isLocked = intent.getBooleanExtra("isLocked", false)
        currentDate = intent.getStringExtra("current_date").toString()

        Log.d("Lock", isLocked.toString())

        // Set the current note ID in ViewModel
        noteViewModel.setNoteId(id)


        /** ----------------- If Editing an Existing Note ----------------- **/
        if (id != 0) {
            binding.activityTitle.text = "Note" // Change title for editing mode
            noteViewModel.getNoteById(id).observe(this) { note ->
                note?.let {
                    // Update title if different
                    if (binding.noteTitleEditText.text.toString() != it.title) {
                        binding.noteTitleEditText.setText(it.title)
                    }
                    // Update content if different
                    if (binding.noteContentEditText.text.toString() != it.content) {
                        binding.noteContentEditText.setText(it.content)
                    }
                }
            }
        }

        /** ----------------- Delete Button Click ----------------- **/
        binding.btnDelete.setOnClickListener {
//            noteViewModel.deleteById(id)
//            CallInterface.onClickListener.onDeleteClickListener()
//            navigate()
//            finish()

//            noteViewModel.getNoteById(id).observe(this) { note ->
                if (isLocked) {
                    isLocked = false
                    saveNote()
                } else {
                    isLocked = true
                    saveNote()
                }
//            }
        }

        /** ----------------- Auto-Save on Text Change ----------------- **/
        binding.noteTitleEditText.addTextChangedListener {
            saveNote()
        }

        binding.noteContentEditText.addTextChangedListener {
            saveNote()
        }

        /** ----------------- Handle Back Press ----------------- **/
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveNote()
                    CallInterface.onClickListener.onClickListener("Note Saved.")
                    finish()
                }
            }
        )
    }

    /**
     * Saves the current note to the database.
     * If the note exists, it updates; otherwise, it inserts a new note.
     */
    private fun saveNote() {
        val note = NoteEntity(
            id = noteViewModel.currentNoteId,
            email = email,
            date = currentDate,
            title = binding.noteTitleEditText.text.toString().trim(),
            content = binding.noteContentEditText.text.toString().trim(),
            isLocked = isLocked
        )
        noteViewModel.insert(note)
    }

    /**
     * Navigates back to the HomeActivity with a message after note deletion.
     */
    private fun navigate() {
        Intent(this, HomeActivity::class.java).also {
            it.putExtra("noteMsg", "Note Deleted")
            startActivity(it)
        }
    }
}
