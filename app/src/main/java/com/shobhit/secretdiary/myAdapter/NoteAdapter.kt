package com.shobhit.secretdiary.myAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shobhit.secretdiary.databinding.NoteLayoutBinding
import com.shobhit.secretdiary.myDataClass.NoteEntity

/**
 * RecyclerView Adapter for displaying a list of notes.
 * Uses ListAdapter with DiffUtil for efficient list updates.
 */
class NoteAdapter(
    private val onNoteClick: (NoteEntity) -> Unit // Callback when a note is clicked
) : ListAdapter<NoteEntity, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    /** ---------------------- Create ViewHolder ---------------------- **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    /** ---------------------- Bind ViewHolder ---------------------- **/
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note, onNoteClick)
    }

    /** ---------------------- ViewHolder Class ---------------------- **/
    class NoteViewHolder(private val binding: NoteLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the note data to the layout views.
         */
        fun bind(note: NoteEntity, onNoteClick: (NoteEntity) -> Unit) {
            binding.textDate.text = note.date
            binding.textTitle.text = note.title
            binding.textDescription.text = note.content

            // Handle click event for a note
            binding.root.setOnClickListener { onNoteClick(note) }

            // Execute pending bindings immediately for performance
            binding.executePendingBindings()
        }
    }

    /** ---------------------- DiffUtil Callback ---------------------- **/
    class DiffCallback : DiffUtil.ItemCallback<NoteEntity>() {

        // Checks if two notes are the same based on their ID
        override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        // Checks if the content of two notes is the same
        override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
