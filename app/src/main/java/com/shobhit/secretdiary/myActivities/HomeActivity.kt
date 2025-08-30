package com.shobhit.secretdiary.myActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shobhit.secretdiary.databinding.ActivityHomeBinding
import com.shobhit.secretdiary.myAdapter.NoteAdapter
import com.shobhit.secretdiary.myInterface.OnClickListener
import com.shobhit.secretdiary.myObject.CallInterface
import com.shobhit.secretdiary.myRepository.NoteRepository
import com.shobhit.secretdiary.myUtilities.NoteDatabase
import com.shobhit.secretdiary.myUtilities.SessionManager
import com.shobhit.secretdiary.myUtilities.showCustomSnackbar
import com.shobhit.secretdiary.myViewModel.NoteViewModel
import com.shobhit.secretdiary.myViewModelFactory.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * HomeActivity - Displays all notes for the logged-in user.
 * Handles note search, adding new notes, and user logout.
 */
class HomeActivity : AppCompatActivity(), OnClickListener {

    // DataBinding for layout access
    private lateinit var binding: ActivityHomeBinding

    // Double back press to exit variables
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    // RecyclerView Adapter for notes
    private lateinit var adapter: NoteAdapter

    // ViewModel for notes
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** ---------------------- View Binding & Theme ---------------------- **/
        binding = ActivityHomeBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        /** ---------------------- Interface for Click Events ---------------------- **/
        CallInterface.onClickListener = this

        /** ---------------------- Snackbars on Login/Note Action ---------------------- **/
        val showSnackbar = intent.getBooleanExtra("showSnackbar", false)
        val noteMsg = intent.getStringExtra("noteMsg")

        noteMsg?.let {
            showCustomSnackbar(binding.root, it)
        }

        if (showSnackbar) {
            showCustomSnackbar(binding.root, "Login Successful")
        }

        /** ---------------------- ViewModel Setup ---------------------- **/
        val email = SessionManager(application).getUserEmail() ?: ""
        val dao = NoteDatabase.getDatabase(this).noteDao()
        val noteRepository = NoteRepository(dao)
        val factory = NoteViewModelFactory(this, noteRepository)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        /** ---------------------- RecyclerView Setup ---------------------- **/
        adapter = NoteAdapter(onNoteClick = { note ->
            Intent(this, NewNoteActivity::class.java).apply {
                putExtra("id", note.id)
                putExtra("email", note.email)
                putExtra("current_date", note.date)
                startActivity(this)
            }
        })

        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        binding.recyclerView.adapter = adapter

        /** ---------------------- Observe Notes List ---------------------- **/
        noteViewModel.getNotes().observe(this) { notes ->
            adapter.submitList(notes) {
                binding.recyclerView.scrollToPosition(0) // Always scroll to top when updated
            }
            if (notes.isEmpty()) {
                binding.noNotesLayout.visibility = View.VISIBLE
            } else {
                binding.noNotesLayout.visibility = View.GONE
            }
        }

        /** ---------------------- Search Notes ---------------------- **/
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            noteViewModel.searchNotes(query).observe(this) { notes ->
                adapter.submitList(notes)
            }
        }

        /** ---------------------- Add New Note ---------------------- **/
        binding.addNotesButton.setOnClickListener {
            Intent(this, NewNoteActivity::class.java).apply {
                putExtra("id", 0)
                putExtra("email", email)
                putExtra("current_date", getCurrentDate())
                startActivity(this)
            }
        }

        /** ---------------------- Logout ---------------------- **/
        binding.logoutLayout.setOnClickListener {
            SessionManager(this).logout()
            Intent(this, AuthActivity::class.java).apply {
                putExtra("showSnackbar", true)
                startActivity(this)
            }
            finish()
        }

        /** ---------------------- Double Back Press to Exit ---------------------- **/
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

    /** ---------------------- Utility: Get Current Date ---------------------- **/
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /** ---------------------- Interface Callbacks ---------------------- **/
    override fun onClickListener(msg: String) {
        showCustomSnackbar(binding.root, msg)
    }

    override fun onDeleteClickListener() {
        finish()
    }
}
