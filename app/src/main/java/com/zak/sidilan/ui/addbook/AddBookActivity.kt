package com.zak.sidilan.ui.addbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityAddBookBinding
import com.zak.sidilan.util.Formatter
import java.util.Calendar
import java.util.TimeZone


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
        Formatter.addThousandSeparator(binding.edPrintPrice)
        Formatter.addThousandSeparator(binding.edSellPrice)
    }

    private fun setAction() {
        binding.edPublishedDate.setOnClickListener {
            showDatePicker(binding.edPublishedDate, getString(R.string.app_name))
        }
        binding.edStartContractDate.setOnClickListener {
            showDatePicker(binding.edStartContractDate, getString(R.string.app_name))
        }
        binding.edEndContractDate.setOnClickListener {
            showDatePicker(binding.edEndContractDate, getString(R.string.app_name))
        }
        // To listen for a checkbox's checked/unchecked state changes
        binding.cbForever.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.edlStartContractDate.isEnabled = !isChecked
            binding.edlEndContractDate.isEnabled = !isChecked
            binding.edStartContractDate.text?.clear()
            binding.edEndContractDate.text?.clear()
        }
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