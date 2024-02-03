package com.zak.sidilan.ui.addbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityAddBookBinding
import com.zak.sidilan.util.Formatter
import java.util.Calendar


class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setAction()
    }


    private fun setView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.title_add_book)
        Formatter.addThousandSeparator(binding.edPrintPrice)
        Formatter.addThousandSeparator(binding.edSellPrice)
    }

    private fun setAction() {
        binding.btnChangeBookPic.setOnClickListener {
            //TODO : Buat Add Picture
            Toast.makeText(this, "Reserved for Adding Picture", Toast.LENGTH_SHORT).show()
        }
        binding.edlIsbn.setEndIconOnClickListener {
            //TODO : Buat Search Book from GoogleBooksAPI
            Toast.makeText(this, "Reserved for Search Book by ISBN", Toast.LENGTH_SHORT).show()
        }
        binding.edPublishedDate.setOnClickListener {
            showDatePicker(binding.edPublishedDate, getString(R.string.app_name))
        }
        binding.edStartContractDate.setOnClickListener {
            showDatePicker(binding.edStartContractDate, getString(R.string.app_name))
        }
        binding.edEndContractDate.setOnClickListener {
            showDatePicker(binding.edEndContractDate, getString(R.string.app_name))
        }
        binding.cbForever.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.edlStartContractDate.isEnabled = !isChecked
            binding.edlEndContractDate.isEnabled = !isChecked
            binding.edStartContractDate.text?.clear()
            binding.edEndContractDate.text?.clear()
        }
        binding.btnAddBook.setOnClickListener {
            //TODO : Buat Add Book
            Toast.makeText(this, "Reserved for Adding Book", Toast.LENGTH_SHORT).show()
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
    private fun showDatePicker(dateEditText: TextView, title: String) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateEditText.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.setTitle(title)
        datePickerDialog.show()
    }
}