package com.zak.sidilan.ui.bookdetail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityBookDetailBinding
import com.zak.sidilan.util.Formatter
import com.zak.sidilan.util.ModalBottomSheetAction
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


val bookDetailActivityModule = module {
    factory { BookDetailActivity() }
}
class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private val viewModel: BookDetailViewModel by viewModel()

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
        setAction()

    }

    private fun setAction() {
        binding.btnEditDelete.setOnClickListener { it ->
            if (it != null) {
                viewModel.bookDetail.observe(this) {
                    val modalBottomSheetAction = ModalBottomSheetAction(2, it, this)
                    modalBottomSheetAction.show(supportFragmentManager, ModalBottomSheetAction.TAG)
                }
            }

        }
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
                binding.tvIsbnValue.text = it.isbn.toString()
                binding.tvAuthorsDetail.text = it.authors.joinToString(", ")
                binding.tvAuthorsValue.text = it.authors.joinToString(", ")
                binding.tvPublishedDateDetail.text = getString(R.string.published_at, Formatter.convertDateFirebaseToDisplay(it.publishedDate))
                binding.tvGenreValue.text = it.genre
                binding.tvPrintPriceValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(it.printPrice))
                binding.tvSellPriceValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(it.sellPrice))
                binding.tvStockQtyValue.text = it.stockQty.toString()
                if (it.isPerpetual) {
                    binding.tvContractValue.text = getString(R.string.forever_contract)
                } else {
                    binding.tvContractValue.text = getString(R.string.contract_date_placeholder, Formatter.convertDateFirebaseToDisplay(it.startContractDate), Formatter.convertDateFirebaseToDisplay(it.endContractDate))
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