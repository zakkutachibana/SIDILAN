package com.zak.sidilan.ui.trx.bookout

import android.app.Activity
import android.app.AlertDialog
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
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.FragmentBookOutTrxSellBinding
import com.zak.sidilan.ui.trx.BookTrxViewModel
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import com.zak.sidilan.ui.trx.choosebook.ChooseBookActivity
import com.zak.sidilan.ui.trx.choosebook.SelectedBooksAdapter
import com.zak.sidilan.util.Formatter
import com.zak.sidilan.util.HawkManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import java.util.Calendar

val bookOutTrxSellFragmentModule = module {
    factory { BookOutTrxSellFragment() }
}

class BookOutTrxSellFragment : Fragment() {

    private var _binding: FragmentBookOutTrxSellBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookTrxViewModel by viewModel()
    private lateinit var adapter: SelectedBooksAdapter
    private lateinit var hawkManager: HawkManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookOutTrxSellBinding.inflate(inflater, container, false)
        hawkManager = HawkManager(requireContext())

        setupView()
        setupViewModel()
        setupListeners()
        setupRecyclerView()
        setAction()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = SelectedBooksAdapter(3, requireContext(), viewModel) { bookPrice ->
            viewModel.getCurrentStock(bookPrice.book.isbn.toString()) { it ->
                val layout =
                    LayoutInflater.from(context).inflate(R.layout.layout_update_stock, null)
                val edStock = layout.findViewById<EditText>(R.id.ed_stock)
                val edlStock = layout.findViewById<TextInputLayout>(R.id.edl_stock)
                layout.findViewById<TextView>(R.id.tv_title_book_stock).text = bookPrice.book.title
                layout.findViewById<TextView>(R.id.tv_author_book_stock).text =
                    bookPrice.book.authors.joinToString(", ")
                layout.findViewById<TextView>(R.id.tv_current_book_stock).text = it.toString()
                layout.findViewById<ImageView>(R.id.iv_book_cover_stock)
                    .load(bookPrice.book.coverUrl)
                edStock.setText(bookPrice.bookQty.toString())
                val dialog = MaterialAlertDialogBuilder(
                    requireActivity(),
                    com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
                )
                    .setTitle(resources.getString(R.string.title_update_stock))
                    .setView(layout)
                    .setIcon(R.drawable.ic_update)
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ya", null)
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    viewModel.getCurrentStock(bookPrice.book.isbn.toString()) { currentStock ->
                        if (edStock.text?.isNotEmpty() == true && edStock.text.toString()
                                .toLong() <= currentStock!! && edStock.text.toString().toLong() != 0L
                        ) {
                            val newQty = edStock.text.toString().toLong()
                            val newCost = newQty * bookPrice.book.printPrice
                            val newBookQtyCost = BookQtyPrice(bookPrice.book, newQty, newCost)
                            viewModel.updateQty(newBookQtyCost)
                            dialog.dismiss()
                        } else {
                            edlStock.error =
                                "${edlStock.hint} tidak boleh kosong atau melebihi stok saat ini"
                        }
                    }
                }
            }
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
                    data?.getParcelableExtra(
                        BookInTrxPrintFragment.EXTRA_BOOK,
                        BookQtyPrice::class.java
                    )?.let { book ->
                        viewModel.addBook(book)
                    }
                } else {
                    data?.getParcelableExtra<BookQtyPrice>(BookInTrxPrintFragment.EXTRA_BOOK)
                        ?.let { book ->
                            viewModel.addBook(book)
                        }
                }
            }
        }

    private fun setupView() {
        Formatter.addThousandSeparatorEditText(binding.edTotalPrice)
        Formatter.addThousandSeparatorEditText(binding.edDiscountAmount)
        Formatter.addThousandSeparatorEditText(binding.edDiscount)
        Formatter.addThousandSeparatorEditText(binding.edFinalPrice)

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

        binding.rgCustomPrice.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_regular -> {
                    binding.edDiscountAmount.removeTextChangedListener(discountAmountWatcher)
                    binding.edDiscount.removeTextChangedListener(discountWatcher)
                    binding.edlDiscount.isEnabled = false
                    binding.edlDiscountAmount.isEnabled = false
                    binding.edlDiscount.visibility = View.GONE
                    binding.edlDiscountAmount.visibility = View.GONE
                    binding.edlTotalPrice.visibility = View.GONE
                    binding.edDiscount.setText("0")
                    binding.edDiscountAmount.setText("0")
                    binding.edFinalPrice.text = binding.edTotalPrice.text
                }

                R.id.rb_percent -> {
                    binding.edDiscountAmount.removeTextChangedListener(discountAmountWatcher)
                    binding.edDiscount.addTextChangedListener(discountWatcher)
                    binding.edlDiscount.visibility = View.VISIBLE
                    binding.edlDiscountAmount.visibility = View.VISIBLE
                    binding.edlTotalPrice.visibility = View.VISIBLE
                    binding.edlDiscount.isEnabled = true
                    binding.edlDiscountAmount.isEnabled = false
                    binding.edDiscountAmount.setText("0")
                    binding.edDiscount.setText("0")
                    binding.edFinalPrice.text = binding.edTotalPrice.text
                }

                R.id.rb_flat -> {
                    binding.edDiscount.removeTextChangedListener(discountWatcher)
                    binding.edDiscountAmount.addTextChangedListener(discountAmountWatcher)
                    binding.edlDiscount.visibility = View.VISIBLE
                    binding.edlDiscountAmount.visibility = View.VISIBLE
                    binding.edlTotalPrice.visibility = View.VISIBLE
                    binding.edlDiscount.isEnabled = false
                    binding.edlDiscountAmount.isEnabled = true
                    binding.edDiscountAmount.setText("0")
                    binding.edDiscount.setText("0")
                    binding.edFinalPrice.text = binding.edTotalPrice.text
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
            intent.putExtra("type", 3)
            getResult.launch(intent)
        }
        binding.edSellDate.setOnClickListener {
            showDatePicker(binding.edSellDate, "Tanggal Cetak Buku")
        }

        binding.btnAddTrx.setOnClickListener {
            if (isValid()) {
                binding.btnAddTrx.isEnabled = false
                val buyerName = binding.edBuyerName.text.toString()
                val address = binding.edAddress.text.toString()
                val sellDate = binding.edSellDate.text.toString()
                val sellingPlatform = binding.edSellPlatform.text.toString()
                val totalPrice = Formatter.getRawValue(binding.edTotalPrice).toLong()
                val finalPrice = Formatter.getRawValue(binding.edFinalPrice).toLong()
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
                            eachBook.book.isbn,
                            eachBook.book.title,
                            eachBook.bookQty,
                            eachBook.book.sellPrice,
                            eachBook.bookPrice
                        )
                    }

                    val logs = Logs(
                        createdBy = createdBy,
                        createdAt = ""
                    )
                    // Create a BookInPrintingTransaction instance
                    val transaction = BookOutSellingTrx(
                        buyerName = buyerName,
                        address = address,
                        bookOutDate = sellDate,
                        sellingPlatform = sellingPlatform,
                        books = bookItems, // Pass the list of BookItems
                        totalBookQty = books.sumOf { it.bookQty }, // Calculate total books quantity
                        totalBookKind = bookItems.size.toLong(),
                        totalPrice = totalPrice, // Use the total price entered by the user
                        finalPrice = finalPrice,
                        discountType = discountType,
                        discountAmount = discountAmount,
                        discountPercent = discountPercent,
                        notes = note
                    )
                    viewModel.addTrxSell(transaction, logs) { _, success ->
                        if (success) {
                            for (bookItem in bookItems) {
                                viewModel.updateStock(
                                    bookItem.isbn.toString(),
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
        binding.edTotalPrice.setText(totalCost.toString())
        binding.edFinalPrice.setText(totalCost.toString())
        binding.edTotalBookKind.setText(kind.toString())
        binding.edTotalBookQty.setText(qty.toString())
    }


    private fun updateDiscount() {
        val totalCost = Formatter.getRawValue(binding.edTotalPrice).toDouble()
        val discount = binding.edDiscount.text.toString().toDouble()
        val discountAmount = totalCost * (discount / 100)
        val finalCost = totalCost - discountAmount
        binding.edDiscountAmount.setText(discountAmount.toLong().toString())
        binding.edFinalPrice.setText(finalCost.toLong().toString())
    }

    private fun updateDiscountAmount() {
        val totalCost = Formatter.getRawValue(binding.edTotalPrice).toDouble()
        val discountAmount = Formatter.getRawValue(binding.edDiscountAmount).toDouble()
        val finalCost = totalCost - discountAmount
        binding.edFinalPrice.setText(finalCost.toLong().toString())
        Log.d("YAM", "tp: $totalCost, da: $discountAmount, fp: $finalCost")
    }

    private fun isValid(): Boolean {
        return isNotEmpty(
            binding.edBuyerName.text.toString(),
            binding.edlBuyerName,
            binding.edBuyerName
        )
                && isNotEmpty(
            binding.edSellDate.text.toString(),
            binding.edlSellDate,
            binding.edSellDate
        )
                && isNotEmpty(
            binding.edTotalPrice.text.toString(),
            binding.edlTotalPrice,
            binding.edTotalPrice
        )
                && isNotZeroOrNegative(
            Formatter.getRawValue(binding.edFinalPrice).toLong(),
            binding.edlFinalPrice,
            binding.edFinalPrice
        )

    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.ed_buyer_name -> {
                    isNotEmpty(
                        binding.edBuyerName.text.toString(),
                        binding.edlBuyerName,
                        binding.edBuyerName
                    )
                }

                R.id.ed_sell_date -> {
                    isNotEmpty(
                        binding.edSellDate.text.toString(),
                        binding.edlSellDate,
                        binding.edSellDate
                    )
                }

                R.id.ed_total_price -> {
                    isNotEmpty(
                        binding.edTotalPrice.text.toString(),
                        binding.edlTotalPrice,
                        binding.edTotalPrice
                    )
                }

                R.id.ed_final_price -> {
                    isNotZeroOrNegative(
                        Formatter.getRawValue(binding.edFinalPrice).toLong(),
                        binding.edlFinalPrice,
                        binding.edFinalPrice
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

    private fun isNotZeroOrNegative(
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
        binding.edBuyerName.addTextChangedListener(TextFieldValidation(binding.edBuyerName))
        binding.edSellDate.addTextChangedListener(TextFieldValidation(binding.edSellDate))
        binding.edDiscount.addTextChangedListener(TextFieldValidation(binding.edDiscount))
        binding.edDiscountAmount.addTextChangedListener(TextFieldValidation(binding.edDiscountAmount))
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