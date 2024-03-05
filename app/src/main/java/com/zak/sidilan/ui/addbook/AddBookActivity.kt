package com.zak.sidilan.ui.addbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.zak.sidilan.R
import com.zak.sidilan.data.Book
import com.zak.sidilan.data.Logs
import com.zak.sidilan.databinding.ActivityAddBookBinding
import com.zak.sidilan.util.Formatter
import java.util.Calendar


class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var reference: DatabaseReference = database.reference.child("books")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setAction()
        setupListeners()

    }


    private fun setView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.title_add_book)
        Formatter.addThousandSeparatorEditText(binding.edPrintPrice)
        Formatter.addThousandSeparatorEditText(binding.edSellPrice)
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
            showDatePicker(binding.edPublishedDate, "Tanggal Terbit Buku")
        }
        binding.edStartContractDate.setOnClickListener {
            showDatePicker(binding.edStartContractDate, "Tanggal Mulai PKS")
        }
        binding.edEndContractDate.setOnClickListener {
            showDatePicker(binding.edEndContractDate, "Tanggal Selesai PKS")
        }
        binding.cbForever.setOnCheckedChangeListener { _, isChecked ->
            binding.edlStartContractDate.isEnabled = !isChecked
            binding.edlEndContractDate.isEnabled = !isChecked
            binding.edStartContractDate.text?.clear()
            binding.edEndContractDate.text?.clear()
        }

        binding.btnAddBook.setOnClickListener {
            if (isValid()) {
                binding.btnAddBook.isEnabled = false
                saveBookToFirebase()
            }
        }
    }


    private fun setupListeners() {
        binding.edIsbn.addTextChangedListener(TextFieldValidation(binding.edIsbn))
        binding.edBookTitle.addTextChangedListener(TextFieldValidation(binding.edBookTitle))
        binding.edAuthors.addTextChangedListener(TextFieldValidation(binding.edAuthors))
        binding.edGenre.addTextChangedListener(TextFieldValidation(binding.edGenre))
        binding.edPublishedDate.addTextChangedListener(TextFieldValidation(binding.edPublishedDate))
        binding.edPrintPrice.addTextChangedListener(TextFieldValidation(binding.edPrintPrice))
        binding.edSellPrice.addTextChangedListener(TextFieldValidation(binding.edSellPrice))
        binding.edStartContractDate.addTextChangedListener(TextFieldValidation(binding.edStartContractDate))
        binding.edEndContractDate.addTextChangedListener(TextFieldValidation(binding.edEndContractDate))
    }
    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.ed_isbn -> {
                    isNotEmpty(binding.edIsbn.text.toString(), binding.edlIsbn, binding.edIsbn)
                }

                R.id.ed_book_title -> {
                    isNotEmpty(binding.edBookTitle.text.toString(), binding.edlBookTitle, binding.edBookTitle)
                }

                R.id.ed_authors -> {
                    isNotEmpty(binding.edAuthors.text.toString(), binding.edlAuthors, binding.edAuthors)
                }

                R.id.ed_genre -> {
                    isNotEmpty(binding.edGenre.text.toString(), binding.edlGenre, binding.edGenre)
                }

                R.id.ed_published_date -> {
                    isNotEmpty(binding.edPublishedDate.text.toString(), binding.edlPublishedDate, binding.edPublishedDate)
                }

                R.id.ed_print_price -> {
                    isNotEmpty(binding.edPrintPrice.text.toString(), binding.edlPrintPrice, binding.edPrintPrice)
                }

                R.id.ed_sell_price -> {
                    isNotEmpty(binding.edSellPrice.text.toString(), binding.edlSellPrice, binding.edSellPrice)
                }

                R.id.ed_start_contract_date -> {
                    if (!binding.cbForever.isChecked){
                        isNotEmpty(binding.edStartContractDate.text.toString(), binding.edlStartContractDate, binding.edStartContractDate)
                    }
                }

                R.id.ed_end_contract_date -> {
                    if (!binding.cbForever.isChecked){
                        isNotEmpty(binding.edEndContractDate.text.toString(), binding.edlEndContractDate, binding.edEndContractDate)
                    }
                }
            }
        }
    }

    private fun saveBookToFirebase() {
        val id = reference.push().key.toString()
        val isbn = binding.edIsbn.text.toString().toLong()
        val title = binding.edBookTitle.text.toString()
        val authors = binding.edAuthors.text.toString().split("\n").map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it }
        val genre = binding.edGenre.text.toString()
        val publishedDate = binding.edPublishedDate.text.toString()
        val printPrice = Formatter.getRawValue(binding.edPrintPrice).toDouble()
        val sellPrice = Formatter.getRawValue(binding.edSellPrice).toDouble()
        val isPerpetual = binding.cbForever.isChecked
        val startContractDate = binding.edStartContractDate.text.toString().ifEmpty { null }
        val endContractDate = binding.edEndContractDate.text.toString().ifEmpty { null }

        val createdBy = binding.userCard.tvUserName.text.toString()
        val createdAt = ServerValue.TIMESTAMP

        val book = Book (
            id, isbn, title, authors, genre, publishedDate, printPrice, sellPrice, isPerpetual, startContractDate, endContractDate
        )
        val logs = Logs(createdBy, createdAt)

        val bookMap = mutableMapOf<String, Any>()
        bookMap["book"] = book
        bookMap["logs"] = logs

        reference.child(id).setValue(bookMap).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Toast.makeText(this, "Added books", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Failed to add books: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.btnAddBook.isEnabled = true
        }
    }
    private fun isValid(): Boolean {
        when (binding.cbForever.isChecked){
            true -> return isNotEmpty(binding.edIsbn.text.toString(), binding.edlIsbn, binding.edIsbn)
                    && isNotEmpty(binding.edBookTitle.text.toString(), binding.edlBookTitle, binding.edBookTitle)
                    && isNotEmpty(binding.edAuthors.text.toString(), binding.edlAuthors, binding.edAuthors)
                    && isNotEmpty(binding.edGenre.text.toString(), binding.edlGenre, binding.edGenre)
                    && isNotEmpty(binding.edPublishedDate.text.toString(), binding.edlPublishedDate, binding.edPublishedDate)
                    && isNotEmpty(binding.edPrintPrice.text.toString(), binding.edlPrintPrice, binding.edPrintPrice)
                    && isNotEmpty(binding.edSellPrice.text.toString(), binding.edlSellPrice, binding.edSellPrice)
            else -> return isNotEmpty(binding.edIsbn.text.toString(), binding.edlIsbn, binding.edIsbn)
                    && isNotEmpty(binding.edBookTitle.text.toString(), binding.edlBookTitle, binding.edBookTitle)
                    && isNotEmpty(binding.edAuthors.text.toString(), binding.edlAuthors, binding.edAuthors)
                    && isNotEmpty(binding.edGenre.text.toString(), binding.edlGenre, binding.edGenre)
                    && isNotEmpty(binding.edPublishedDate.text.toString(), binding.edlPublishedDate, binding.edPublishedDate)
                    && isNotEmpty(binding.edPrintPrice.text.toString(), binding.edlPrintPrice, binding.edPrintPrice)
                    && isNotEmpty(binding.edSellPrice.text.toString(), binding.edlSellPrice, binding.edSellPrice)
                    && isNotEmpty(binding.edStartContractDate.text.toString(), binding.edlStartContractDate, binding.edStartContractDate)
                    && isNotEmpty(binding.edEndContractDate.text.toString(), binding.edlEndContractDate, binding.edEndContractDate)
        }

    }

    private fun isNotEmpty(value: String, textInputLayout: TextInputLayout, editText: EditText): Boolean {
        when {
            value.trim().isEmpty() -> {
                textInputLayout.error = "Input " + textInputLayout.hint.toString() + " diperlukan"
                editText.requestFocus()
                return false
            }
            else -> {
                textInputLayout.isErrorEnabled = false
            }
        }
        return true
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