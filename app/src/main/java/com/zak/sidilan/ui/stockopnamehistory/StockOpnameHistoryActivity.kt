package com.zak.sidilan.ui.stockopnamehistory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.sidilan.databinding.ActivityStockOpnameHistoryBinding
import com.zak.sidilan.ui.stockopname.StockOpnameViewModel
import com.zak.sidilan.ui.stockopnamedetail.StockOpnameDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class StockOpnameHistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockOpnameHistoryBinding
    private lateinit var adapter: StockOpnameHistoryAdapter
    private val viewModel: StockOpnameViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupRecyclerView()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.title = "Riwayat Stock Opname"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViewModel() {
        val currentDate = LocalDate.now()
        val yearFormat = DateTimeFormatter.ofPattern("yyyy", Locale.getDefault())
        val yearName = currentDate.format(yearFormat)
        viewModel.getStockOpnames(yearName)

        viewModel.stockOpname.observe(this) { stockOpnames ->
            adapter.submitList(stockOpnames)
        }
    }
    private fun setupAction() {
        binding.edStockOpnamePeriod.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called as and when the text is being changed
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                viewModel.getStockOpnames(newText)
            }
        })
    }
    private fun setupRecyclerView() {
        adapter = StockOpnameHistoryAdapter(this) { stockOpname ->
            val intent = Intent(this, StockOpnameDetailActivity::class.java)
            intent.putExtra("stock_opname", stockOpname)
            startActivity(intent)
        }
        binding.rvPeriod.layoutManager = LinearLayoutManager(this)
        binding.rvPeriod.adapter = adapter
        binding.rvPeriod.itemAnimator = DefaultItemAnimator()
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