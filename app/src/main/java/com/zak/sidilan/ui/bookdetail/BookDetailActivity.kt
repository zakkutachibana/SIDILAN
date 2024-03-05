package com.zak.sidilan.ui.bookdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityAddBookBinding
import com.zak.sidilan.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var reference: DatabaseReference = database.reference.child("books")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
    }

    private fun setView() {
        val bookId = intent.getStringExtra("bookId")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Buku $bookId"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}