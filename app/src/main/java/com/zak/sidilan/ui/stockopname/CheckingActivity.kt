package com.zak.sidilan.ui.stockopname

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.databinding.ActivityCheckingBinding
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import com.zak.sidilan.util.CheckBoxState
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckingBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var adapter: CheckingBookAdapter
    private val viewModel: StockOpnameViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Periksa Buku"
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.peekHeight = 130
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.bottomSheetLayout.tvChecked
    }
    private fun setupRecyclerView() {
        adapter = CheckingBookAdapter(this) { bookOpname, position ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Periksa Stok")
                .setMessage("Apakah benar buku \"${bookOpname.bookTitle}\" memiliki stok sebanyak \"${bookOpname.stockExpected} buku\" ?")
                .setPositiveButton(getString(R.string.appropriate)) { dialog, _ ->
                    bookOpname.isAppropriate = true
                    bookOpname.reason = null
                    bookOpname.discrepancy = null
                    viewModel.updateBookOpname(bookOpname)
                    adapter.notifyItemChanged(position)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.inappropriate)) { dialog, _ ->
                    discrepancy(bookOpname, position)
                    dialog.dismiss()
                }
                .show()
        }
        binding.rvCheckBook.layoutManager = LinearLayoutManager(this)
        binding.rvCheckBook.adapter = adapter
        binding.rvCheckBook.itemAnimator = DefaultItemAnimator()
    }
    private fun discrepancy(bookOpname: BookOpname, position: Int) {
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_discrepancy, null)
        val edActual = layout.findViewById<EditText>(R.id.ed_actual_stock)
        val edExpected = layout.findViewById<EditText>(R.id.ed_expected_stock)
        val edReason = layout.findViewById<EditText>(R.id.ed_reason)
        val edlActual = layout.findViewById<TextInputLayout>(R.id.edl_actual_stock)
        val edlReason = layout.findViewById<TextInputLayout>(R.id.edl_reason)
        edExpected.setText(bookOpname.stockExpected.toString())
        val dialog = MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle("Stok Tidak Sesuai")
            .setView(layout)
            .setIcon(R.drawable.ic_discrepancy)
            .setMessage("Saat ini buku \"${bookOpname.bookTitle}\" seharusnya memiliki stok sebanyak \"${bookOpname.stockExpected} buku\". Namun ada berapa stok fisik sesungguhnya?")
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.adjust), null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val discrepancy = edActual.text.toString().toInt() - bookOpname.stockExpected?.toInt()!!
            if (edActual.text.isNotEmpty() && edReason.text.isNotEmpty() && discrepancy != 0) {
                bookOpname.isAppropriate = false
                bookOpname.stockActual = edActual.text.toString().toLong()
                bookOpname.reason = edReason.text.toString()
                bookOpname.discrepancy = discrepancy
                viewModel.updateBookOpname(bookOpname)
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            } else {
                when {
                    edActual.text.isEmpty() -> edlActual.error = " "
                    edReason.text.isEmpty() -> edlReason.error = " "
                    discrepancy == 0 -> edlActual.error = " "
                    else -> {
                        edlReason.error = " "
                        edlActual.error = " "
                    }
                }
            }
        }
    }
    private fun setupViewModel() {
        viewModel.getBooks()
        viewModel.bookOpnameList.observe(this) { books ->
            val bookSize = books.size
            val bookChecked = books.count { it.isAppropriate != null }
            val discrepancy = books.sumOf { it.discrepancy ?: 0 }
            binding.bottomSheetLayout.tvItems.text = bookSize.toString()
            binding.bottomSheetLayout.tvChecked.text = bookChecked.toString()
            binding.bottomSheetLayout.tvItem2Value.text = discrepancy.toString()
            adapter.submitList(books)
            when (bookSize == bookChecked) {
                true -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.bottomSheetLayout.btnDone.isEnabled = true
                }
                else -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.bottomSheetLayout.btnDone.isEnabled = false
                }
            }
        }

    }

    private fun setupAction() {
        binding.bottomSheetLayout.btnDone.setOnClickListener {
            viewModel.bookOpnameList.observe(this) { books ->
                val intent = Intent().apply {
                    putParcelableArrayListExtra(StockOpnameActivity.OPNAME_LIST, ArrayList(books))
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
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
}