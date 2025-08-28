package com.shobhit.secretdiary.myAdapter

import com.shobhit.secretdiary.myDataClass.NoteEntity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shobhit.secretdiary.databinding.AddNoteLayoutBinding
import com.shobhit.secretdiary.databinding.NoteLayoutBinding

class NoteAdapter(private val onNoteClick: (NoteEntity) -> Unit) : ListAdapter<NoteEntity, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {
        val note = getItem(position)
        holder.bind(note, onNoteClick)
    }


    class NoteViewHolder(private val binding: NoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteEntity, onNoteClick: (NoteEntity) -> Unit) {
            binding.textDate.text = note.date
            binding.textTitle.text = note.title
            binding.textDescription.text = note.content
            binding.root.setOnClickListener { onNoteClick(note) }
            binding.executePendingBindings()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NoteEntity>() {
        override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem == newItem

        }
    }
}
