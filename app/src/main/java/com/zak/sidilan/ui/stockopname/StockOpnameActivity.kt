package com.zak.sidilan.ui.stockopname

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.entities.StockOpname
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.ActivityStockOpnameBinding
import com.zak.sidilan.ui.books.BooksViewModel
import com.zak.sidilan.ui.trx.BookTrxViewModel
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import com.zak.sidilan.util.HawkManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class StockOpnameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockOpnameBinding
    private lateinit var hawkManager : HawkManager
    private lateinit var bookOpnameList: List<BookOpname>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hawkManager = HawkManager(this)

        setupView()
        setupAction()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        // Get current month name
        val currentDate = LocalDate.now()
        val monthFormat = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        val monthYearFormat = DateTimeFormatter.ofPattern("MM/yyyy", Locale.getDefault())
        val fullFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val yearFormat = DateTimeFormatter.ofPattern("yyyy", Locale.getDefault())
        val monthName = currentDate.format(monthFormat)
        val monthYearName = currentDate.format(monthYearFormat)
        val yearName = currentDate.format(yearFormat)
        val fullDate = currentDate.format(fullFormat)

        // Set month name to EditText
        binding.edOpnamePeriod.setText(monthName)
        binding.edOpnameDate.setText(fullDate)
        binding.tvYear.text = "Tahun $yearName"

        val currentUser = hawkManager.retrieveData<User>("user")

        binding.userCard.tvUserName.text = currentUser?.displayName
        binding.userCard.tvUserAction.text = "Diperiksa Oleh"
        binding.userCard.ivProfilePicture.load(currentUser?.photoUrl)

        supportActionBar?.title = "Stock Opname"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setExtraView(bookOpname: ArrayList<BookOpname>) {
        binding.layoutStartChecking.root.visibility = View.GONE
        val bookSize = bookOpname.size
        val bookAppropriate = bookOpname.count { it.isAppropriate == true }
        val bookInappropriate = bookOpname.count { it.isAppropriate == false }
        val discrepancy = bookOpname.sumOf { it.discrepancy ?: 0 }
        val expectedStock = bookOpname.sumOf { it.stockExpected ?:0 }
        val actualStock = expectedStock + discrepancy
        binding.layoutOverview.tvBookItemValue.text = getString(R.string.book_count, bookSize.toString())
        binding.layoutOverview.tvBookMatchValue.text = getString(R.string.book_count, bookAppropriate.toString())
        binding.layoutOverview.tvBookDiscrepancyValue.text = getString(R.string.book_count, bookInappropriate.toString())
        binding.layoutOverview.tvStockDiffValue.text = when {
            discrepancy > 0 -> getString(R.string.plus_qty, discrepancy.toString())
            else -> getString(R.string.total_stock_qty, discrepancy.toString())
        }
        binding.layoutOverview.tvExpectedStockValue.text = getString(R.string.total_stock_qty, expectedStock.toString())
        binding.layoutOverview.tvActualStockValue.text = getString(R.string.total_stock_qty, actualStock.toString())
        binding.layoutOverview.root.visibility = View.VISIBLE
        binding.btnDone.isEnabled = true
    }

    private fun setupAction() {
        binding.cardCheckStock.setOnClickListener {
            val intent = Intent(this, CheckingActivity::class.java)
            getResult.launch(intent)
        }
        binding.btnDone.setOnClickListener {
            val createdBy = hawkManager.retrieveData<User>("user")?.id.toString()
            val stockOpname = StockOpname(
                books = bookOpnameList,
                date = binding.edOpnameDate.text.toString(),
                logs = Logs(createdBy, null)
            )
            Toast.makeText(this, "$stockOpname", Toast.LENGTH_LONG).show()
            Log.d("StockOpname", "$stockOpname")
        }
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.getParcelableArrayListExtra<BookOpname>(OPNAME_LIST, BookOpname::class.java)?.let { books ->
                        setExtraView(books)
                        bookOpnameList = books
                    }
                } else {
                    data?.getParcelableArrayListExtra<BookOpname>(OPNAME_LIST)?.let { books ->
                        setExtraView(books)
                        bookOpnameList = books
                    }
                }
            }
        }
    companion object {
        val OPNAME_LIST = "opname_list"
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