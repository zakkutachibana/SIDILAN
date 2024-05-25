package com.zak.sidilan.ui.stockopname

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityStockOpnameBinding

class StockOpnameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockOpnameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameBinding.inflate(layoutInflater)
        setupView()
        setupAction()
        setContentView(binding.root)
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupAction() {
        binding.cardCheckStock.setOnClickListener {
            val intent = Intent(this, CheckingActivity::class.java)
            startActivity(intent)
        }
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