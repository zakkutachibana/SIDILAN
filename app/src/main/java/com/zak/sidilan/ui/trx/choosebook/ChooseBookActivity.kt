package com.zak.sidilan.ui.trx.choosebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.search.SearchView
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.VolumeInfo
import com.zak.sidilan.databinding.ActivityChooseBookBinding
import com.zak.sidilan.ui.books.BooksViewModel
import com.zak.sidilan.ui.bottomsheets.ModalBottomSheetView
import com.zak.sidilan.ui.trx.bookin.BookInTrxOtherFragment
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import com.zak.sidilan.ui.trx.bookout.BookOutTrxOtherFragment
import com.zak.sidilan.ui.trx.bookout.BookOutTrxSellFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseBookActivity : AppCompatActivity(), ModalBottomSheetView.BottomSheetListener {
    private lateinit var binding: ActivityChooseBookBinding
    private val viewModel: BooksViewModel by viewModel()
    private lateinit var adapter: ChooseBookAdapter
    private var type: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra("type", 0)
        setupView()
        setupAction()
        setupRecyclerView()
        setupViewModel()

    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Pilih Buku"
    }
    private fun setupAction() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Handle search text change
                s?.let {
                    viewModel.filterBooks(s.toString())

                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = ChooseBookAdapter(this) { bookDetail ->
            showBottomSheet(bookDetail)
        }
        binding.rvChooseBook.layoutManager = GridLayoutManager(this, 3)
        binding.rvChooseBook.adapter = adapter
        binding.rvChooseBook.itemAnimator = DefaultItemAnimator()
    }

    private fun setupViewModel() {
        viewModel.getBooks()
        viewModel.bookList.observe(this) { books ->
            adapter.submitList(books)
        }
    }

    private fun showBottomSheet(book: BookDetail) {
        when (type) {
            1->{
                val modalBottomSheetView = ModalBottomSheetView(3, null, book)
                modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
                modalBottomSheetView.setBottomSheetListener(this)
            }
            2->{
                val modalBottomSheetView = ModalBottomSheetView(3, null, book)
                modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
                modalBottomSheetView.setBottomSheetListener(this)
            }
            3->{
                val modalBottomSheetView = ModalBottomSheetView(4, null, book)
                modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
                modalBottomSheetView.setBottomSheetListener(this)
            }
            4->{
                val modalBottomSheetView = ModalBottomSheetView(4, null, book)
                modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
                modalBottomSheetView.setBottomSheetListener(this)
            }
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

    override fun onButtonClicked(volumeInfo: VolumeInfo?, book: Book?, bookQty: Long?) {
        val intent = when (type) {
            //Type 1: Book In Print
            1 -> {
                val subtotal = book?.printPrice!! * bookQty!!
                val bookQtyPrice = BookQtyPrice(book, bookQty, subtotal)
                Intent().apply {
                    putExtra(BookInTrxPrintFragment.EXTRA_BOOK, bookQtyPrice)
                }
            }
            //Type 2: Book In Donation
            2 -> {
                val subtotal = book?.printPrice!! * bookQty!!
                val bookQtyPrice = BookQtyPrice(book, bookQty, subtotal)
                Intent().apply {
                    putExtra(BookInTrxOtherFragment.EXTRA_BOOK, bookQtyPrice)
                }
            }

            3 -> {
                val subtotal = book?.printPrice!! * bookQty!!
                val bookQtyPrice = BookQtyPrice(book, bookQty, subtotal)
                Intent().apply {
                    putExtra(BookOutTrxSellFragment.EXTRA_BOOK, bookQtyPrice)
                }
            }

            4 -> {
                val subtotal = book?.printPrice!! * bookQty!!
                val bookQtyPrice = BookQtyPrice(book, bookQty, subtotal)
                Intent().apply {
                    putExtra(BookOutTrxOtherFragment.EXTRA_BOOK, bookQtyPrice)
                }
            }
            else -> Intent().apply { }

        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDismissed() {}

}