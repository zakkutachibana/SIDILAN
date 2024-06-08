package com.zak.sidilan.ui.stockopnamedetail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.StockOpname
import com.zak.sidilan.databinding.ActivityStockOpnameDetailBinding
import com.zak.sidilan.ui.stockopname.StockOpnameViewModel
import com.zak.sidilan.util.Formatter
import org.koin.androidx.viewmodel.ext.android.viewModel

class StockOpnameDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockOpnameDetailBinding
    private lateinit var adapter : BookOpnameAdapter
    private lateinit var stockOpname: StockOpname
    private lateinit var bookOpnameList: List<BookOpname>

    private val viewModel: StockOpnameViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameDetailBinding.inflate(layoutInflater)
        adapter = BookOpnameAdapter(this)

        setContentView(binding.root)

        val stockOpname: StockOpname? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("stock_opname", StockOpname::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("stock_opname")
        }

        if (stockOpname != null) {
            this.stockOpname = stockOpname
        }

        stockOpname?.let { stockOpnameDetail ->
            bookOpnameList = stockOpnameDetail.books.values.filterNotNull()
            adapter.submitList(bookOpnameList.sortedBy{ it.isAppropriate != false }.sortedBy { it.isAppropriate != null })
        }

        setupView()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        viewModel.getUserById(stockOpname.logs?.createdBy.toString())
        viewModel.user.observe(this) { user ->
            binding.userCard.tvUserName.text = getString(R.string.by_at, user?.displayName, user?.role, Formatter.convertEpochToLocal(stockOpname.logs?.createdAt))
            binding.userCard.ivProfilePicture.load(user?.photoUrl)
        }
    }

    private fun setupRecyclerView() {
        binding.rvBooksOpname.layoutManager = LinearLayoutManager(this)
        binding.rvBooksOpname.adapter = adapter
        binding.rvBooksOpname.itemAnimator = DefaultItemAnimator()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Stok Opname"

        val discrepancy = bookOpnameList.sumOf { it.discrepancy ?: 0 }
        binding.tvTotalValue.text = getString(R.string.total_stock_qty, bookOpnameList.sumOf { it.stockActual ?: 0 }.toString())
        binding.tvBookKindValue.text = getString(R.string.book_count, bookOpnameList.size.toString())
        binding.tvValueDiscrepancy.text = when {
            (discrepancy > 0) -> getString(R.string.plus_qty, discrepancy.toString())
            else -> getString(R.string.total_stock_qty, discrepancy.toString())
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