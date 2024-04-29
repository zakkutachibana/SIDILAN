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
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.FragmentBookInTrxPrintBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
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
    private val viewModel: BookInTrxViewModel by viewModel()
    private lateinit var adapter: SelectedBooksAdapter
    private lateinit var hawkManager : HawkManager


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
        adapter = SelectedBooksAdapter(requireContext(), viewModel) { bookPrice ->
            val intent = Intent(requireActivity(), BookDetailActivity::class.java)
            intent.putExtra("bookId", bookPrice.book?.id)
            requireActivity().startActivity(intent)
        }
        binding.rvBookInPrint.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
//                binding.btnAddItem.isEnabled = false
                val shopName = binding.edPrintShop.text.toString()
                val printDate = binding.edPrintDate.text.toString()
                val totalPrice = Formatter.getRawValue(binding.edTotalPrice).toLong()
                val note = binding.edNote.text.toString()
                val isCustomPrice = when {
                    binding.rbPercent.isChecked -> true
                    binding.rbFlat.isChecked -> true
                    binding.rbRegular.isChecked -> false
                    else -> false
                }
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

    private fun calculateTotalPrice(bookList: List<BookQtyPrice>) {
        var totalPrice: Long = 0
        for (bookPrice in bookList) {
            bookPrice.bookPrice.let { totalPrice += it }
        }
        binding.edTotalPrice.setText(totalPrice.toString())
        binding.edFinalPrice.setText(totalPrice.toString())
    }


    private fun updateDiscount() {
        val totalPrice = Formatter.getRawValue(binding.edTotalPrice).toDouble()
        val discount = binding.edDiscount.text.toString().toDouble()
        val discountAmount = totalPrice * (discount / 100)
        val finalPrice = totalPrice - discountAmount
        binding.edDiscountAmount.setText(discountAmount.toLong().toString())
        binding.edFinalPrice.setText(finalPrice.toLong().toString())
    }

    private fun updateDiscountAmount() {
        val totalPrice = Formatter.getRawValue(binding.edTotalPrice).toDouble()
        val discountAmount = Formatter.getRawValue(binding.edDiscountAmount).toDouble()
        val finalPrice = totalPrice - discountAmount
        binding.edFinalPrice.setText(finalPrice.toLong().toString())
        Log.d("YAM", "tp: $totalPrice, da: $discountAmount, fp: $finalPrice")
    }


    private fun isValid(): Boolean {
//        when (binding.cbCustomPrice.isChecked) {
//            true ->
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
                binding.edTotalPrice.text.toString(),
                binding.edlTotalPrice,
                binding.edTotalPrice
            )

//            else -> return isNotEmpty(
//                binding.edPrintShop.text.toString(),
//                binding.edlPrintShop,
//                binding.edPrintShop
//            )
//                    && isNotEmpty(
//                binding.edPrintDate.text.toString(),
//                binding.edlPrintDate,
//                binding.edPrintDate
//            )
//                    && isNotEmpty(
//                binding.edTotalPrice.text.toString(),
//                binding.edlTotalPrice,
//                binding.edTotalPrice
//            )
//                    && isNotEmpty(
//                binding.edNote.text.toString(),
//                binding.edlNote,
//                binding.edNote
//            )
//        }
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
        binding.edDiscount.addTextChangedListener(TextFieldValidation(binding.edDiscount))
        binding.edDiscountAmount.addTextChangedListener(TextFieldValidation(binding.edDiscountAmount))
        binding.edTotalPrice.addTextChangedListener(TextFieldValidation(binding.edTotalPrice))
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