package com.zak.sidilan.ui.trxhistory

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityTrxHistoryBinding
import com.zak.sidilan.ui.users.UserDetailActivity
import com.zak.sidilan.util.FirstItemMarginDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrxHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrxHistoryBinding
    private val viewModel: TrxHistoryViewModel by viewModel()
    private lateinit var adapter: BookTrxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrxHistoryBinding.inflate(layoutInflater)

        setupView()
        setupViewModel()
        setupRecyclerView()
        setContentView(binding.root)
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.title_book_trx_history)
        supportActionBar?.elevation = 0f
    }
    private fun setupRecyclerView() {
        adapter = BookTrxAdapter(this) { user ->
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra("userId", user.id)
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