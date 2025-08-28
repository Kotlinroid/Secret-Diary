package com.shobhit.secretdiary.myActivities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.shobhit.secretdiary.databinding.ActivityNewNoteBinding
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myInterface.OnClickListener
import com.shobhit.secretdiary.myObject.CallInterface
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myUtilities.NoteDatabase
import com.shobhit.secretdiary.myUtilities.SessionManager
import com.shobhit.secretdiary.myViewModel.NoteViewModel
import com.shobhit.secretdiary.myViewModelFactory.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewNoteActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewNoteBinding
    lateinit var noteViewModel: NoteViewModel
    lateinit var email: String
    lateinit var currentDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        val dao = NoteDatabase.getDatabase(this).noteDao()
        val noteRepository = NoteRepository(dao)
        val factory = NoteViewModelFactory(this, noteRepository)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        binding.backButtonLayout.setOnClickListener {
            saveNote()
            CallInterface.onClickListener.onClickListener("Note Saved.")
            finish()
        }

        val id = intent.getIntExtra("id", 0)
        email = intent.getStringExtra("email").toString()
        currentDate = intent.getStringExtra("current_date").toString()

        noteViewModel.setNoteId(id)

        if (id != 0) {
            binding.activityTitle.text = "Note"
            noteViewModel.getNoteById(id).observe(this) { note ->
                note?.let {
                    if (binding.noteTitleEditText.text.toString() != it.title) {
                        binding.noteTitleEditText.setText(it.title)
                    }
                    if (binding.noteContentEditText.text.toString() != it.content) {
                        binding.noteContentEditText.setText(it.content)
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            noteViewModel.deleteById(id)
            CallInterface.onClickListener.onDeleteClickListener()
            navigate()
            finish()
        }

        binding.noteTitleEditText.addTextChangedListener {
            saveNote()
        }

        binding.noteContentEditText.addTextChangedListener {
            saveNote()
        }

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

    fun saveNote(){
        val note = NoteEntity(
            id = noteViewModel.currentNoteId,
            email = email,
            date = currentDate,
            title = binding.noteTitleEditText.text.toString(),
            content = binding.noteContentEditText.text.toString())
        noteViewModel.insert(note)
    }

    fun navigate(){
        Intent(this, HomeActivity::class.java).also {
            it.putExtra("noteMsg", "Note Deleted")
            startActivity(it)
        }
    }
}