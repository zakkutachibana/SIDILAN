package com.zak.sidilan.ui.trx.bookin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.databinding.FragmentBookInTrxPrintBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import com.zak.sidilan.ui.trx.choosebook.ChooseBookActivity
import com.zak.sidilan.ui.trx.choosebook.SelectedBooksAdapter
import com.zak.sidilan.util.Formatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import java.util.Calendar

val bookInTrxPrintFragmentModule = module {
    factory { BookInTrxPrintFragment() }
}
class BookInTrxPrintFragment : Fragment() {
    private var _binding: FragmentBookInTrxPrintBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookInTrxViewModel by viewModel()
    private lateinit var adapter: SelectedBooksAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookInTrxPrintBinding.inflate(inflater, container, false)

        setupView()
        setupViewModel()
        setupListeners()
        setupRecyclerView()
        setAction()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = SelectedBooksAdapter(requireContext(), viewModel) { bookPrice ->
            val intent = Intent(requireActivity(), BookDetailActivity::class.java)
            intent.putExtra("bookId", bookPrice.book?.id)
            requireActivity().startActivity(intent)
        }
        binding.rvBookInPrint.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBookInPrint.adapter = adapter
        binding.rvBookInPrint.itemAnimator = DefaultItemAnimator()

        viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
            adapter.updateBooks(books)
            calculateTotalPrice(books)
        }
    }

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.getParcelableExtra(EXTRA_BOOK, BookQtyPrice::class.java)?.let { book ->
                        viewModel.addBook(book)
                    }
                } else {
                    data?.getParcelableExtra<BookQtyPrice>(EXTRA_BOOK)?.let { book ->
                        viewModel.addBook(book)
                    }
                }
            }
        }

    private fun setupView() {
        Formatter.addThousandSeparatorEditText(binding.edTotalPrice)
    }

    private fun setupViewModel() {
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAction() {
        binding.btnAddItem.setOnClickListener {
            val intent = Intent(requireContext(), ChooseBookActivity::class.java)
            intent.putExtra("type", 1)
            getResult.launch(intent)
        }
        binding.edPrintDate.setOnClickListener {
            showDatePicker(binding.edPrintDate, "Tanggal Cetak Buku")
        }
        binding.cbCustomPrice.setOnCheckedChangeListener { _, isChecked ->
            binding.edlTotalPrice.isEnabled = isChecked
            if (isChecked) {
                binding.edTotalPrice.text?.clear()
            } else {
                viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
                    calculateTotalPrice(books)
                }
            }
        }
        binding.btnAddTrx.setOnClickListener {
            if (isValid()) {
//                binding.btnAddItem.isEnabled = false
                val shopName = binding.edPrintShop.text.toString()
                val printDate = binding.edPrintDate.text.toString()
                val totalPrice = Formatter.getRawValue(binding.edTotalPrice).toLong()
                val note = binding.edNote.text.toString()
                val isCustomPrice = binding.cbCustomPrice.isChecked
                viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
                    val bookItems = books.map { eachBook ->
                        BookSubtotal(
                            bookId = eachBook.book.id,
                            qty = eachBook.bookQty,
                            subtotalPrice = eachBook.bookPrice
                        )
                    }

                    // Create a BookInPrintingTransaction instance
                    val transaction = BookInPrintingTrx(
                        printingShop = shopName,
                        bookInDate = printDate,
                        books = bookItems, // Pass the list of BookItems
                        totalBooksQty = books.sumOf { it.bookQty }, // Calculate total books quantity
                        totalAmount = totalPrice, // Use the total price entered by the user
                        isCustomCost = isCustomPrice,
                        notes = note
                    )
                    viewModel.addTrxPrint(transaction)
                }
            }
        }
    }

    private fun calculateTotalPrice(bookList : List<BookQtyPrice>) {
        var totalPrice: Long = 0
        for (bookPrice in bookList) {
            bookPrice.bookPrice.let { totalPrice += it }
        }
        binding.edTotalPrice.setText(totalPrice.toString())
    }
    private fun isValid(): Boolean {
        when (binding.cbCustomPrice.isChecked) {
            true -> return isNotEmpty(
                binding.edPrintShop.text.toString(),
                binding.edlPrintShop,
                binding.edPrintShop
            )
                    && isNotEmpty(
                binding.edPrintDate.text.toString(),
                binding.edlPrintDate,
                binding.edPrintDate
            )
                    && isNotEmpty(
                binding.edTotalPrice.text.toString(),
                binding.edlTotalPrice,
                binding.edTotalPrice
            )

            else -> return isNotEmpty(
                binding.edPrintShop.text.toString(),
                binding.edlPrintShop,
                binding.edPrintShop
            )
                    && isNotEmpty(
                binding.edPrintDate.text.toString(),
                binding.edlPrintDate,
                binding.edPrintDate
            )
                    && isNotEmpty(
                binding.edTotalPrice.text.toString(),
                binding.edlTotalPrice,
                binding.edTotalPrice
            )
                    && isNotEmpty(
                binding.edNote.text.toString(),
                binding.edlNote,
                binding.edNote
            )
        }
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.ed_print_shop -> {
                    isNotEmpty(binding.edPrintShop.text.toString(),
                        binding.edlPrintShop,
                        binding.edPrintShop
                    )
                }

                R.id.ed_print_date -> {
                    isNotEmpty(
                        binding.edPrintDate.text.toString(),
                        binding.edlPrintDate,
                        binding.edPrintDate
                    )
                }
                R.id.ed_print_date -> {
                    isNotEmpty(
                        binding.edPrintDate.text.toString(),
                        binding.edlPrintDate,
                        binding.edPrintDate
                    )
                }
                R.id.ed_print_date -> {
                    isNotEmpty(
                        binding.edPrintDate.text.toString(),
                        binding.edlPrintDate,
                        binding.edPrintDate
                    )
                }
            }
        }
    }

    private fun isNotEmpty(
        value: String,
        textInputLayout: TextInputLayout,
        editText: EditText
    ): Boolean {
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

    private fun setupListeners() {
        binding.edPrintShop.addTextChangedListener(TextFieldValidation(binding.edPrintShop))
        binding.edPrintDate.addTextChangedListener(TextFieldValidation(binding.edPrintDate))
    }

    private fun showDatePicker(dateEditText: TextView, title: String) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
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
    companion object {
        const val EXTRA_BOOK = "extra_book"
    }
}