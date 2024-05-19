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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.FragmentBookInTrxPrintBinding
import com.zak.sidilan.ui.trx.BookTrxViewModel
import com.zak.sidilan.ui.trx.choosebook.ChooseBookActivity
import com.zak.sidilan.ui.trx.choosebook.SelectedBooksAdapter
import com.zak.sidilan.util.Formatter
import com.zak.sidilan.util.HawkManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import java.util.Calendar

val bookInTrxPrintFragmentModule = module {
    factory { BookInTrxPrintFragment() }
}

class BookInTrxPrintFragment : Fragment() {
    private var _binding: FragmentBookInTrxPrintBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookTrxViewModel by viewModel()
    private lateinit var adapter: SelectedBooksAdapter
    private lateinit var hawkManager: HawkManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookInTrxPrintBinding.inflate(inflater, container, false)
        hawkManager = HawkManager(requireContext())

        setupView()
        setupViewModel()
        setupListeners()
        setupRecyclerView()
        setAction()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = SelectedBooksAdapter(1, requireContext(), viewModel) { bookCost ->
            val layout = LayoutInflater.from(context).inflate(R.layout.layout_update_stock, null)
            val edStock = layout.findViewById<EditText>(R.id.ed_stock)
            layout.findViewById<TextView>(R.id.tv_title_book_stock).text = bookCost.book.title
            layout.findViewById<TextView>(R.id.tv_author_book_stock).text =
                bookCost.book.authors.joinToString(", ")
            layout.findViewById<ImageView>(R.id.iv_book_cover_stock).load(bookCost.book.coverUrl)
            edStock.setText(bookCost.bookQty.toString())
            MaterialAlertDialogBuilder(
                requireActivity(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle(resources.getString(R.string.title_update_stock))
                .setView(layout)
                .setIcon(R.drawable.ic_update)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ya") { dialog, which ->
                    val newQty = edStock.text.toString().toLong()
                    val newCost = newQty * bookCost.book.printPrice
                    val newBookQtyCost = BookQtyPrice(bookCost.book, newQty, newCost)
                    viewModel.updateQty(newBookQtyCost)
                }
                .show()
        }
        binding.rvBookInPrint.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBookInPrint.adapter = adapter
        binding.rvBookInPrint.itemAnimator = DefaultItemAnimator()

        viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
            adapter.updateBooks(books)
            calculateTotalCost(books)
        }
    }

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
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
        Formatter.addThousandSeparatorEditText(binding.edTotalCost)
        Formatter.addThousandSeparatorEditText(binding.edDiscountAmount)
        Formatter.addThousandSeparatorEditText(binding.edDiscount)
        Formatter.addThousandSeparatorEditText(binding.edFinalCost)

        val discountAmountWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.edDiscountAmount.text?.length == 0) {
                    binding.edDiscountAmount.setText("0")
                }
                updateDiscountAmount()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        val discountWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.edDiscount.text?.length == 0) {
                    binding.edDiscount.setText("0")
                }
                updateDiscount()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.rgCustomCost.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_regular -> {
                    binding.edDiscountAmount.removeTextChangedListener(discountAmountWatcher)
                    binding.edDiscount.removeTextChangedListener(discountWatcher)
                    binding.edlDiscount.isEnabled = false
                    binding.edlDiscountAmount.isEnabled = false
                    binding.edlDiscount.visibility = View.GONE
                    binding.edlDiscountAmount.visibility = View.GONE
                    binding.edlTotalCost.visibility = View.GONE
                    binding.edDiscount.setText("0")
                    binding.edDiscountAmount.setText("0")
                    binding.edFinalCost.text = binding.edTotalCost.text
                }

                R.id.rb_percent -> {
                    binding.edDiscountAmount.removeTextChangedListener(discountAmountWatcher)
                    binding.edDiscount.addTextChangedListener(discountWatcher)
                    binding.edlDiscount.visibility = View.VISIBLE
                    binding.edlDiscountAmount.visibility = View.VISIBLE
                    binding.edlTotalCost.visibility = View.VISIBLE
                    binding.edlDiscount.isEnabled = true
                    binding.edlDiscountAmount.isEnabled = false
                    binding.edDiscountAmount.setText("0")
                    binding.edDiscount.setText("0")
                    binding.edFinalCost.text = binding.edTotalCost.text
                }

                R.id.rb_flat -> {
                    binding.edDiscount.removeTextChangedListener(discountWatcher)
                    binding.edDiscountAmount.addTextChangedListener(discountAmountWatcher)
                    binding.edlDiscount.visibility = View.VISIBLE
                    binding.edlDiscountAmount.visibility = View.VISIBLE
                    binding.edlTotalCost.visibility = View.VISIBLE
                    binding.edlDiscount.isEnabled = false
                    binding.edlDiscountAmount.isEnabled = true
                    binding.edDiscountAmount.setText("0")
                    binding.edDiscount.setText("0")
                    binding.edFinalCost.text = binding.edTotalCost.text
                }
            }
        }
        val currentUser = hawkManager.retrieveData<User>("user")

        binding.userCard.tvUserName.text = currentUser?.displayName
        binding.userCard.ivProfilePicture.load(currentUser?.photoUrl)

    }

    private fun setupViewModel() {
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        viewModel.isBookListEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.btnAddTrx.isEnabled = !isEmpty
            binding.rbFlat.isEnabled = !isEmpty
            binding.rbPercent.isEnabled = !isEmpty
            binding.rbRegular.isEnabled = !isEmpty
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

        binding.btnAddTrx.setOnClickListener {
            if (isValid()) {
                binding.btnAddTrx.isEnabled = false
                val shopName = binding.edPrintShop.text.toString()
                val address = binding.edAddress.text.toString()
                val printDate = binding.edPrintDate.text.toString()
                val totalCost = Formatter.getRawValue(binding.edTotalCost).toLong()
                val finalCost = Formatter.getRawValue(binding.edFinalCost).toLong()
                val discountAmount = Formatter.getRawValue(binding.edDiscountAmount).toLong()
                val discountPercent = Formatter.getRawValue(binding.edDiscount).toLong()
                val note = binding.edNote.text.toString()
                val discountType = when {
                    binding.rbPercent.isChecked -> "percent"
                    binding.rbFlat.isChecked -> "flat"
                    binding.rbRegular.isChecked -> "none"
                    else -> "none"
                }
                val createdBy = hawkManager.retrieveData<User>("user")?.id.toString()


                viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
                    val bookItems = books.map { eachBook ->
                        BookSubtotal(
                            eachBook.book.id,
                            eachBook.book.title,
                            eachBook.bookQty,
                            eachBook.book.printPrice,
                            eachBook.bookPrice
                        )
                    }

                    val logs = Logs(
                        createdBy = createdBy,
                        createdAt = ""
                    )
                    // Create a BookInPrintingTransaction instance
                    val transaction = BookInPrintingTrx(
                        printingShopName = shopName,
                        bookInDate = printDate,
                        address = address,
                        books = bookItems, // Pass the list of BookItems
                        totalBookQty = books.sumOf { it.bookQty }, // Calculate total books quantity
                        totalBookKind = bookItems.size.toLong(),
                        totalCost = totalCost, // Use the total price entered by the user
                        finalCost = finalCost,
                        discountType = discountType,
                        discountAmount = discountAmount,
                        discountPercent = discountPercent,
                        notes = note
                    )
                    viewModel.addTrxPrint(transaction, logs) { _, success ->
                        if (success) {
                            for (bookItem in bookItems) {
                                viewModel.updateStock(
                                    bookItem.bookId,
                                    transaction.type,
                                    bookItem.qty
                                )
                            }
                            requireActivity().finish()
                        } else {
                            binding.btnAddTrx.isEnabled = true
                        }

                    }
                }
            }
        }
    }

    private fun calculateTotalCost(bookList: List<BookQtyPrice>) {
        var totalCost: Long = 0
        var qty: Long = 0
        val kind = bookList.size
        for (bookCost in bookList) {
            bookCost.bookPrice.let { totalCost += it }
            bookCost.bookQty.let { qty += it }
        }
        binding.edTotalCost.setText(totalCost.toString())
        binding.edFinalCost.setText(totalCost.toString())
        binding.edTotalBookKind.setText(kind.toString())
        binding.edTotalBookQty.setText(qty.toString())
    }


    private fun updateDiscount() {
        val totalCost = Formatter.getRawValue(binding.edTotalCost).toDouble()
        val discount = binding.edDiscount.text.toString().toDouble()
        val discountAmount = totalCost * (discount / 100)
        val finalCost = totalCost - discountAmount
        binding.edDiscountAmount.setText(discountAmount.toLong().toString())
        binding.edFinalCost.setText(finalCost.toLong().toString())
    }

    private fun updateDiscountAmount() {
        val totalCost = Formatter.getRawValue(binding.edTotalCost).toDouble()
        val discountAmount = Formatter.getRawValue(binding.edDiscountAmount).toDouble()
        val finalCost = totalCost - discountAmount
        binding.edFinalCost.setText(finalCost.toLong().toString())
    }


    private fun isValid(): Boolean {
        return isNotEmpty(
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
            binding.edTotalCost.text.toString(),
            binding.edlTotalCost,
            binding.edTotalCost
        )
                && isNotNegative(
            Formatter.getRawValue(binding.edFinalCost).toLong(),
            binding.edlFinalCost,
            binding.edFinalCost
        )
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.ed_print_shop -> {
                    isNotEmpty(
                        binding.edPrintShop.text.toString(),
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
                R.id.ed_final_cost -> {
                    isNotNegative(
                        Formatter.getRawValue(binding.edFinalCost).toLong(),
                        binding.edlFinalCost,
                        binding.edFinalCost
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

    private fun isNotNegative(
        value: Long,
        textInputLayout: TextInputLayout,
        editText: EditText
    ): Boolean {
        when {
            value <= 0 -> {
                textInputLayout.error = "Harga Akhir tidak boleh ≤ 0"
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
        binding.edDiscount.addTextChangedListener(TextFieldValidation(binding.edDiscount))
        binding.edDiscountAmount.addTextChangedListener(TextFieldValidation(binding.edDiscountAmount))
        binding.edTotalCost.addTextChangedListener(TextFieldValidation(binding.edTotalCost))
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