package com.apex.flashcard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apex.flashcard.databinding.ItemFlashcardBinding
import com.apex.flashcard.model.Flashcard

class FlashcardAdapter(
    private val onDelete: (Flashcard) -> Unit
) : ListAdapter<Flashcard, FlashcardAdapter.ViewHolder>(FlashcardDiffCallback()) {

    inner class ViewHolder(
        private val binding: ItemFlashcardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isShowingQuestion = true
        private var currentFlashcard: Flashcard? = null

        init {
            binding.root.setOnClickListener {
                currentFlashcard?.let { flashcard ->
                    isShowingQuestion = !isShowingQuestion
                    binding.textCardContent.text = if (isShowingQuestion) {
                        flashcard.question
                    } else {
                        flashcard.answer
                    }
                }
            }

            binding.btnDelete.setOnClickListener {
                currentFlashcard?.let(onDelete)
            }
        }

        fun bind(flashcard: Flashcard) {
            currentFlashcard = flashcard
            isShowingQuestion = true
            binding.textCardContent.text = flashcard.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFlashcardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class FlashcardDiffCallback : DiffUtil.ItemCallback<Flashcard>() {
    override fun areItemsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
        return oldItem == newItem
    }
}
