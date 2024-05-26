package com.zak.sidilan.ui.stockopname

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import coil.load
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.ActivityStockOpnameBinding
import com.zak.sidilan.util.HawkManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class StockOpnameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockOpnameBinding
    private lateinit var hawkManager : HawkManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameBinding.inflate(layoutInflater)
        hawkManager = HawkManager(this)

        setupView()
        setupAction()
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        // Get current month name
        val currentDate = LocalDate.now()
        val monthFormat = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        val monthYearFormat = DateTimeFormatter.ofPattern("MM/yyyy", Locale.getDefault())
        val yearFormat = DateTimeFormatter.ofPattern("yyyy", Locale.getDefault())
        val monthName = currentDate.format(monthFormat)
        val monthYearName = currentDate.format(monthYearFormat)
        val yearName = currentDate.format(yearFormat)

        // Set month name to EditText
        binding.edOpnamePeriod.setText(monthName)
        binding.edOpnameDate.setText(monthYearName)
        binding.tvYear.text = "Tahun $yearName"

        val currentUser = hawkManager.retrieveData<User>("user")

        binding.userCard.tvUserName.text = currentUser?.displayName
        binding.userCard.tvUserAction.text = "Diperiksa Oleh"
        binding.userCard.ivProfilePicture.load(currentUser?.photoUrl)

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