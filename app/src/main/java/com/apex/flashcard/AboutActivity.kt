package com.apex.flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apex.flashcard.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
