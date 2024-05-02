package com.zak.sidilan.ui.trx.bookout

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.zak.sidilan.data.entities.BookInDonationTrx
import com.zak.sidilan.data.entities.BookOutDonationTrx
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.FragmentBookOutTrxOtherBinding
import com.zak.sidilan.ui.trx.BookTrxViewModel
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import com.zak.sidilan.ui.trx.choosebook.ChooseBookActivity
import com.zak.sidilan.ui.trx.choosebook.SelectedBooksAdapter
import com.zak.sidilan.util.HawkManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class BookOutTrxOtherFragment : Fragment() {
    private var _binding: FragmentBookOutTrxOtherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookTrxViewModel by viewModel()
    private lateinit var adapter: SelectedBooksAdapter
    private lateinit var hawkManager : HawkManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookOutTrxOtherBinding.inflate(inflater, container, false)
        hawkManager = HawkManager(requireContext())

        setupView()
        setupViewModel()
        setupListeners()
        setupRecyclerView()
        setAction()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = SelectedBooksAdapter(4, requireContext(), viewModel) { bookPrice ->
            val layout = LayoutInflater.from(context).inflate(R.layout.layout_update_stock, null)
            val edStock = layout.findViewById<EditText>(R.id.ed_stock)
            layout.findViewById<TextView>(R.id.tv_title_book_stock).text = bookPrice.book.title
            layout.findViewById<TextView>(R.id.tv_author_book_stock).text = bookPrice.book.authors.joinToString(", ")
            layout.findViewById<ImageView>(R.id.iv_book_cover_stock).load(bookPrice.book.coverUrl)
            edStock.setText(bookPrice.bookQty.toString())
            MaterialAlertDialogBuilder(requireActivity(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle(resources.getString(R.string.title_update_stock))
                .setView(layout)
                .setIcon(R.drawable.ic_update)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ya") { dialog, which ->
                    val newQty = edStock.text.toString().toLong()
                    val newPrice = newQty * bookPrice.book.printPrice
                    val newBookQtyPrice = BookQtyPrice(bookPrice.book, newQty, newPrice)
                    viewModel.updateQty(newBookQtyPrice)
                }
                .show()
        }
        binding.rvBookOutOther.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBookOutOther.adapter = adapter
        binding.rvBookOutOther.itemAnimator = DefaultItemAnimator()

        viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
            adapter.updateBooks(books)
            calculateTotalQty(books)
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
                    data?.getParcelableExtra(BookInTrxPrintFragment.EXTRA_BOOK, BookQtyPrice::class.java)?.let { book ->
                        viewModel.addBook(book)
                    }
                } else {
                    data?.getParcelableExtra<BookQtyPrice>(BookInTrxPrintFragment.EXTRA_BOOK)?.let { book ->
                        viewModel.addBook(book)
                    }
                }
            }
        }

    private fun setupView() {
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
        }
    }
    private fun setAction() {
        binding.btnAddItem.setOnClickListener {
            val intent = Intent(requireContext(), ChooseBookActivity::class.java)
            intent.putExtra("type", 4)
            getResult.launch(intent)
        }
        binding.edDoneeDate.setOnClickListener {
            showDatePicker(binding.edDoneeDate, "Tanggal Cetak Buku")
        }
        binding.btnAddTrx.setOnClickListener {
            if (isValid()) {
                binding.btnAddTrx.isEnabled = false
                val doneeName = binding.edDoneeName.text.toString()
                val doneeDate = binding.edDoneeDate.text.toString()
                val note = binding.edNote.text.toString()

                viewModel.selectedBooksList.observe(viewLifecycleOwner) { books ->
                    val bookItems = books.map { eachBook ->
                        BookSubtotal(
                            bookId = eachBook.book.id,
                            qty = eachBook.bookQty,
                            subtotalPrice = eachBook.bookPrice
                        )
                    }

                    // Create a BookInPrintingTransaction instance
                    val transaction = BookOutDonationTrx(
                        doneeName = doneeName,
                        bookOutDate = doneeDate,
                        books = bookItems, // Pass the list of BookItems
                        totalBookQty = books.sumOf { it.bookQty }, // Calculate total books quantity
                        totalBookKind = bookItems.size.toLong(),
                        notes = note
                    )
                    viewModel.addTrxOutDonation(transaction) { _, success ->
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
    private fun calculateTotalQty(bookList: List<BookQtyPrice>) {
        var qty: Long = 0
        val kind = bookList.size
        for (bookPrice in bookList) {
            bookPrice.bookQty.let { qty += it }
        }

        binding.edTotalBookKind.setText(kind.toString())
        binding.edTotalBookQty.setText(qty.toString())
    }

    private fun isValid(): Boolean {
        return isNotEmpty(
            binding.edDoneeName.text.toString(),
            binding.edlDoneeName,
            binding.edDoneeName
        )
                && isNotEmpty(
            binding.edDoneeDate.text.toString(),
            binding.edlDoneeDate,
            binding.edDoneeDate
        )
    }
    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.ed_donee_name -> {
                    isNotEmpty(
                        binding.edDoneeName.text.toString(),
                        binding.edlDoneeName,
                        binding.edDoneeName
                    )
                }

                R.id.ed_donee_date -> {
                    isNotEmpty(
                        binding.edDoneeDate.text.toString(),
                        binding.edlDoneeDate,
                        binding.edDoneeDate
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
        binding.edDoneeName.addTextChangedListener(TextFieldValidation(binding.edDoneeName))
        binding.edDoneeDate.addTextChangedListener(TextFieldValidation(binding.edDoneeDate))
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