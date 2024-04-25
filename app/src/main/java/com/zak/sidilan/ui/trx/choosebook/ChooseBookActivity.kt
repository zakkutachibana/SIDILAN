package com.zak.sidilan.ui.trx.choosebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookPrice
import com.zak.sidilan.data.entities.VolumeInfo
import com.zak.sidilan.databinding.ActivityChooseBookBinding
import com.zak.sidilan.ui.books.BooksViewModel
import com.zak.sidilan.ui.bottomsheets.ModalBottomSheetView
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseBookActivity : AppCompatActivity(), ModalBottomSheetView.BottomSheetListener {
    private lateinit var binding: ActivityChooseBookBinding
    private val viewModel: BooksViewModel by viewModel()
    private lateinit var adapter: ChooseBookAdapter
    private var type: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBookBinding.inflate(layoutInflater)

        type = intent.getIntExtra("type", 0)
        setupView()
        setupRecyclerView()
        setupViewModel()

        setContentView(binding.root)
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Pilih Buku"
    }

    private fun setupRecyclerView() {
        adapter = ChooseBookAdapter(this) { book ->
            showBottomSheet(book)
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

    private fun showBottomSheet(book: Book) {
        val modalBottomSheetView = ModalBottomSheetView(3, null, book)
        modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
        modalBottomSheetView.setBottomSheetListener(this)
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

    override fun onButtonClicked(volumeInfo: VolumeInfo?, book: Book?, bookQty: Int?) {
        val intent = when (type) {
            1 -> {
                val subtotal = book?.printPrice!! * bookQty!!
                val bookPrice = BookPrice(book, bookQty, subtotal)
                Intent().apply {
                    putExtra(BookInTrxPrintFragment.EXTRA_BOOK, bookPrice)
                }
            }

            2 -> Intent().apply {
                putExtra(BookInTrxPrintFragment.EXTRA_BOOK, book)
            }

            3 -> Intent().apply {
                putExtra(BookInTrxPrintFragment.EXTRA_BOOK, book)
            }

            4 -> Intent().apply {
                putExtra(BookInTrxPrintFragment.EXTRA_BOOK, book)
            }

            else -> Intent().apply { }

        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDismissed() {}

}