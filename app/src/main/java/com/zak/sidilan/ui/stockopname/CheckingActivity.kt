package com.zak.sidilan.ui.stockopname

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.databinding.ActivityCheckingBinding
import com.zak.sidilan.ui.books.BooksViewModel
import com.zak.sidilan.ui.trx.choosebook.ChooseBookAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckingBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var adapter: CheckingBookAdapter
    private val viewModel: BooksViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckingBinding.inflate(layoutInflater)
        setupView()
        setupRecyclerView()
        setupViewModel()
        setContentView(binding.root)
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.peekHeight = 130
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.bottomSheetLayout.tvChecked
    }
    private fun setupRecyclerView() {
        adapter = CheckingBookAdapter(this)
        binding.rvCheckBook.layoutManager = LinearLayoutManager(this)
        binding.rvCheckBook.adapter = adapter
        binding.rvCheckBook.itemAnimator = DefaultItemAnimator()
    }

    private fun setupViewModel() {
        viewModel.getBooks()
        viewModel.bookList.observe(this) { books ->
            binding.bottomSheetLayout.tvItems.text = books.size.toString()
            adapter.submitList(books)
        }
        val adapter = CheckingBookAdapter(this)
// Assuming you have set up your RecyclerView with this adapter and populated it with data

        val checkedItemCount = adapter.countCheckedItems()
        println("Number of checked items: $checkedItemCount")
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
}