package com.shobhit.secretdiary.myActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shobhit.secretdiary.R
import com.shobhit.secretdiary.databinding.ActivityHomeBinding
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myInterface.OnClickListener
import com.shobhit.secretdiary.myObject.CallInterface
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myUtilities.NoteDatabase
import com.shobhit.secretdiary.myUtilities.SessionManager
import com.shobhit.secretdiary.myUtilities.showCustomSnackbar
import com.shobhit.secretdiary.myViewModel.NoteViewModel
import com.shobhit.secretdiary.myViewModelFactory.NoteViewModelFactory
import androidx.core.widget.addTextChangedListener
import com.shobhit.secretdiary.myAdapter.NoteAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivityHomeBinding
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast
    lateinit var adapter: NoteAdapter
    lateinit var noteViewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        CallInterface.onClickListener = this

        // Set up of Login Successful SnackBar
        val showSnackbar = intent.getBooleanExtra("showSnackbar", false)

        val noteMsg = intent.getStringExtra("noteMsg")
        if (noteMsg != null) {
            showCustomSnackbar(binding.root, noteMsg)
        }
        if (showSnackbar) {
            showCustomSnackbar(binding.root, "Login Successful")
        }

        val email = SessionManager(application).getUserEmail() ?: ""
        val dao = NoteDatabase.getDatabase(this).noteDao()
        val noteRepository = NoteRepository(dao)
        val factory = NoteViewModelFactory(this, noteRepository)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        adapter = NoteAdapter(onNoteClick = { note ->
            Intent(this, NewNoteActivity::class.java).also {
                it.putExtra("id", note.id)
                it.putExtra("email", note.email)
                it.putExtra("current_date", note.date)
                startActivity(it)
            }
        })

        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        binding.recyclerView.adapter = adapter

        noteViewModel.getNotes().observe(this) {
            adapter.submitList(it) {
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            noteViewModel.searchNotes(query).observe(this) { notes ->
                adapter.submitList(notes)
            }
        }

        binding.addNotesButton.setOnClickListener {
            Intent(this, NewNoteActivity::class.java).also {
                it.putExtra("id", 0)
                it.putExtra("email", email)
                it.putExtra("current_date", getCurrentDate())
                startActivity(it)

            }
        }

        binding.logoutLayout.setOnClickListener {
            SessionManager(this).logout()
            Intent(this, AuthActivity::class.java).also {
                it.putExtra("showSnackbar", true)
                startActivity(it)
            }
            finish()
        }

        toast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        toast.cancel()
                        finish()
                    } else {
                        toast.show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
        )
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onClickListener(msg: String) {
        showCustomSnackbar(binding.root, msg)

    }

    override fun onDeleteClickListener() {
        finish()
    }
}