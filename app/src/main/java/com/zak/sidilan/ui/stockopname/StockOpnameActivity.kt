package com.zak.sidilan.ui.stockopname

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.entities.StockOpname
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.ActivityStockOpnameBinding
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
    private val viewModel: StockOpnameViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hawkManager = HawkManager(this)

        setupView()
        setupViewModel()
        setupAction()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        val currentDate = LocalDate.now()
        val monthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
        val fullFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("id", "ID"))
        val monthName = currentDate.format(monthFormat)
        val fullDate = currentDate.format(fullFormat)

        binding.edOpnamePeriod.setText(monthName)
        binding.edOpnameDate.setText(fullDate)

        val currentUser = hawkManager.retrieveData<User>("user")

        binding.userCard.tvUserName.text = currentUser?.displayName
        binding.userCard.tvUserAction.text = "Diperiksa Oleh"
        binding.userCard.ivProfilePicture.load(currentUser?.photoUrl)

        supportActionBar?.title = "Stock Opname"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViewModel() {
        val currentDate = LocalDate.now()
        val monthYearFormat = DateTimeFormatter.ofPattern("yyyy/MM", Locale.getDefault())
        val monthYearName = currentDate.format(monthYearFormat)
        viewModel.checkDraftStockOpname(monthYearName) { currentStockOpname ->
            if (currentStockOpname != null) {
                when (currentStockOpname.status) {
                    "Done" -> {
                        binding.layoutOverview.statusLabelText.text = "Selesai"
                        binding.layoutOverview.statusLabel.backgroundTintList = ContextCompat.getColorStateList(this, R.color.safe_green)
                        setExtraView(currentStockOpname.books.values.filterNotNull())
                    }
                    "Draft" -> {
                        binding.layoutOverview.statusLabelText.text = "Draf"
                        binding.layoutOverview.statusLabel.backgroundTintList = ContextCompat.getColorStateList(this, R.color.safe_yellow)
                        setExtraView(currentStockOpname.books.values.filterNotNull())
                    }
                    else -> {
                        binding.layoutOverview.statusLabelText.text = "Belum Diperiksa"
                        binding.layoutOverview.statusLabel.backgroundTintList = ContextCompat.getColorStateList(this, R.color.safe_red)
                    }
                }

            } else {
                binding.layoutOverview.statusLabelText.text = "Belum Dimulai"
                binding.layoutOverview.statusLabel.backgroundTintList = ContextCompat.getColorStateList(this, R.color.safe_red)
            }
        }
    }

    private fun setExtraView(bookOpname: List<BookOpname>) {
        val bookSize = bookOpname.size
        val bookAppropriate = bookOpname.count { it.isAppropriate == true }
        val bookInappropriate = bookOpname.count { it.isAppropriate == false }
        val discrepancy = bookOpname.sumOf { it.discrepancy ?: 0 }
        val expectedStock = bookOpname.sumOf { it.stockExpected ?:0 }
        val checkedBook = bookOpname.count { it.isAppropriate != null}
        val actualStock = expectedStock + discrepancy
        binding.layoutOverview.tvBookItemValue.text = getString(R.string.checked_count, checkedBook.toString(), bookSize.toString())
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAction() {
        val currentDate = LocalDate.now()
        val monthYearFormat = DateTimeFormatter.ofPattern("yyyy/MM", Locale.getDefault())
        val monthYearName = currentDate.format(monthYearFormat)

        binding.cardCheckStock.setOnClickListener {
            val intent = Intent(this, CheckingActivity::class.java)
            intent.putExtra("currentMonth", monthYearName.toString())
            getResult.launch(intent)
        }
        binding.btnDone.setOnClickListener {
            val createdBy = hawkManager.retrieveData<User>("user")?.id.toString()
            val date = binding.edOpnameDate.text.toString()
            val books = bookOpnameList
            val overallAppropriate = books.all { it.isAppropriate == true }
            val status = when {
                books.all { it.isAppropriate == null } -> "Unchecked"
                books.any { it.isAppropriate == null } -> "Draft"
                books.all { it.isAppropriate != null } -> "Done"
                else -> ""
            }

            viewModel.saveStockOpname(date, books, monthYearName, createdBy, overallAppropriate, status) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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

    companion object {
        const val OPNAME_LIST = "extra_book"
    }
}