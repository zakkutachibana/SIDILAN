package com.zak.sidilan.ui.bookdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityBookDetailBinding
import com.zak.sidilan.util.Formatter

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private val viewModel: BookDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = intent.getStringExtra("bookId")
        if (bookId != null) {
            viewModel.getBookDetailById(bookId)
            println("id: $bookId")
        }
        setView()
        setViewModel()

    }

    private fun setView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Buku"
    }

    private fun setViewModel() {
        viewModel.bookDetail.observe(this) {
            if (it != null) {
                binding.tvBookTitleDetail.text = it.title
                binding.tvBookTitleValue.text = it.title
                binding.tvAuthorsDetail.text = it.authors.joinToString(", ")
                binding.tvAuthorsValue.text = it.authors.joinToString(", ")
                binding.tvPublishedDateDetail.text = getString(R.string.published_at, Formatter.convertDateFormat(it.publishedDate))
                binding.tvGenreValue.text = it.genre
                binding.tvPrintPriceValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(it.printPrice))
                binding.tvSellPriceValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(it.sellPrice))
                if (it.isPerpetual) {
                    binding.tvContractValue.text = getString(R.string.forever_contract)
                } else {
                    binding.tvContractValue.text = getString(R.string.contract_date_placeholder, Formatter.convertDateFormat(it.startContractDate), Formatter.convertDateFormat(it.endContractDate))
                }
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