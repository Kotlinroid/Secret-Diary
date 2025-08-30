package com.shobhit.secretdiary.myAdapter

import android.graphics.BlurMaskFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shobhit.secretdiary.R
import com.shobhit.secretdiary.databinding.NoteLayoutBinding
import com.shobhit.secretdiary.myDataClass.NoteEntity
import com.shobhit.secretdiary.myFragments.FingerprintDialogFragment
import com.shobhit.secretdiary.myViewModel.NoteViewModel

class NoteAdapter(
    private val onNoteClick: (NoteEntity) -> Unit,
    private val fragmentManager: FragmentManager,
    private val viewModel: NoteViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<NoteEntity, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding, viewModel, lifecycleOwner, fragmentManager)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note, onNoteClick)
    }

    class NoteViewHolder(
        val binding: NoteLayoutBinding,
        private val viewModel: NoteViewModel,
        private val lifecycleOwner: LifecycleOwner,
        private val fragmentManager: FragmentManager,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteEntity, onNoteClick: (NoteEntity) -> Unit) {
            binding.textDate.text = note.date
            binding.textTitle.text = note.title
            binding.textDescription.text = note.content
            var isLocked = note.isLocked

            viewModel.getNoteById(note.id).observe(lifecycleOwner) { diary ->
                if (diary.isLocked){
                    binding.iconLock.visibility = View.VISIBLE
                }else{
                    binding.iconLock.visibility = View.GONE
                }
                // Handle click on whole note
                binding.root.setOnClickListener {
                    if (isLocked){
                        val dialog = FingerprintDialogFragment{success ->
                            if (success) {
                                isLocked = false
                                removeBlur()
                                onNoteClick(diary)
                            }
                        }
                        dialog.show(fragmentManager, "fingerprint_dialog")
                    }else{
                        onNoteClick(note)
                    }
                }
            }
                if (isLocked) {
                    applyBlur()
                } else {
                    removeBlur()
                }


            // Handle lock icon click
            binding.iconLock.setOnClickListener {
                if (isLocked) {
                    val dialog = FingerprintDialogFragment{success ->
                        if (success) {
                            isLocked = false
                            removeBlur()
                        }
                    }
                    dialog.show(fragmentManager, "fingerprint_dialog")

                } else {
                    applyBlur()
                    // if note is already unlocked, lock it again in DB
                    isLocked = true
                }
            }



            binding.executePendingBindings()
        }

        private fun applyBlur() {
            binding.iconLock.setImageResource(R.drawable.lock_24px)
            binding.textDescription.setLayerType(ViewGroup.LAYER_TYPE_SOFTWARE, null)
            binding.textDescription.paint.maskFilter =
                BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
            binding.textDescription.invalidate()
        }

        private fun removeBlur() {
            binding.textDescription.paint.maskFilter = null
            binding.textDescription.invalidate()
            binding.iconLock.setImageResource(R.drawable.lock_open_right_24px)
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
