package com.zak.sidilan.ui.trxhistory

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityTrxHistoryBinding
import com.zak.sidilan.ui.trxdetail.TrxDetailActivity
import com.zak.sidilan.util.FirstItemMarginDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrxHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrxHistoryBinding
    private val viewModel: TrxHistoryViewModel by viewModel()
    private lateinit var adapter: BookTrxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrxHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupRecyclerView()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.title_book_trx_history)
        supportActionBar?.elevation = 0f

    }
    private fun setupAction() {
        binding.cgFilters.setOnCheckedStateChangeListener { group, _ ->
            val selectedTrx = mutableListOf<String>()
            val chipToTrxTypeMap = mapOf(
                R.id.chip_book_in_print to "TRXP",
                R.id.chip_book_out_sell to "TRXS",
                R.id.chip_book_in_other to "TRXI",
                R.id.chip_book_out_other to "TRXO"
            )
            for (chipId in group.checkedChipIds) {
                val trxType = chipToTrxTypeMap[chipId]
                if (trxType != null) {
                    selectedTrx.add(trxType)
                }
            }
            viewModel.filterTrxByType(selectedTrx)
        }
    }
    private fun setupRecyclerView() {
        adapter = BookTrxAdapter(this) { trxDetail ->
            val intent = Intent(this, TrxDetailActivity::class.java)
            intent.putExtra("trxId", trxDetail.bookTrx?.id)
            startActivity(intent)
        }
        val pixel = resources.getDimensionPixelOffset(R.dimen.first_item_margin)
        val decorator = FirstItemMarginDecoration(pixel)
        binding.rvBookTrx.layoutManager = LinearLayoutManager(this)
        binding.rvBookTrx.adapter = adapter
        binding.rvBookTrx.itemAnimator = DefaultItemAnimator()
        binding.rvBookTrx.addItemDecoration(decorator)
    }

    private fun setupViewModel() {
        viewModel.getTrx()
        viewModel.trxList.observe(this) { bookTrx ->
            adapter.submitList(bookTrx)
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