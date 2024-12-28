package com.apex.flashcard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.apex.flashcard.adapter.FlashcardAdapter
import com.apex.flashcard.data.FlashcardDatabase
import com.apex.flashcard.databinding.ActivityMainBinding
import com.apex.flashcard.databinding.DialogAddCardBinding
import com.apex.flashcard.model.Flashcard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FlashcardAdapter
    private val database by lazy { FlashcardDatabase.getDatabase(this) }
    private val dao by lazy { database.flashcardDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Flashcards "

        setupAdapter()
        setupButtons()
        observeFlashcards()
        checkAndInsertDefaultCard()
    }

    private fun checkAndInsertDefaultCard() {
        lifecycleScope.launch {
            val cards = dao.getAllFlashcards().first()
            if (cards.isEmpty()) {
                val aboutCard = Flashcard(
                    question = "Welcome to Minimalist Flashcards! ",
                    answer = """
                        This app embodies minimalism in learning:
                        
                        • Simple, distraction-free interface 
                        • Focus on what matters - your learning 
                        • Easy to create and manage flashcards 
                        • Perfect for quick study sessions 
                        
                        Made with  for minimalist learners
                        
                        Tap anywhere on the card to flip it! 
                    """.trimIndent()
                )
                val howToCard = Flashcard(
                    question = "How to use this app? ",
                    answer = """
                        • Tap '+' to add new flashcards 
                        • Tap anywhere on a card to flip it 
                        • Use arrows to navigate 
                        • Tap 'Delete' to remove cards 
                        
                        Simple, clean, effective! 
                        
                        Start creating your flashcards now! 
                    """.trimIndent()
                )
                dao.insertFlashcard(aboutCard)
                dao.insertFlashcard(howToCard)
            }
        }
    }

    private fun setupAdapter() {
        adapter = FlashcardAdapter(
            onDelete = { flashcard ->
                showDeleteConfirmation(flashcard)
            }
        )
        binding.flashcardPager.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnPrevious.setOnClickListener {
            if (binding.flashcardPager.currentItem > 0) {
                binding.flashcardPager.currentItem = binding.flashcardPager.currentItem - 1
            }
        }

        binding.btnNext.setOnClickListener {
            if (binding.flashcardPager.currentItem < adapter.itemCount - 1) {
                binding.flashcardPager.currentItem = binding.flashcardPager.currentItem + 1
            }
        }

        binding.fabAdd.setOnClickListener {
            showAddCardDialog()
        }

        // Update navigation buttons state
        binding.flashcardPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNavigationButtons(position)
            }
        })
    }

    private fun updateNavigationButtons(position: Int) {
        binding.btnPrevious.isEnabled = position > 0
        binding.btnNext.isEnabled = position < adapter.itemCount - 1
        binding.btnPrevious.alpha = if (position > 0) 1.0f else 0.5f
        binding.btnNext.alpha = if (position < adapter.itemCount - 1) 1.0f else 0.5f
    }

    private fun observeFlashcards() {
        lifecycleScope.launch {
            dao.getAllFlashcards().collectLatest { flashcards ->
                adapter.submitList(flashcards)
                updateNavigationButtons(binding.flashcardPager.currentItem)
            }
        }
    }

    private fun showAddCardDialog() {
        val dialogBinding = DialogAddCardBinding.inflate(layoutInflater)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Add New Flashcard")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val question = dialogBinding.editQuestion.text.toString()
                val answer = dialogBinding.editAnswer.text.toString()
                
                if (question.isNotBlank() && answer.isNotBlank()) {
                    lifecycleScope.launch {
                        dao.insertFlashcard(
                            Flashcard(
                                question = question,
                                answer = answer
                            )
                        )
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(flashcard: Flashcard) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Flashcard")
            .setMessage("Are you sure you want to delete this flashcard?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    dao.deleteFlashcard(flashcard)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}