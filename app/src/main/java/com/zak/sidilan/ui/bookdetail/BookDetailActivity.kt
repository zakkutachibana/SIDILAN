package com.zak.sidilan.ui.bookdetail

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
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
        viewModel.bookDetail.observe(this) { bookDetail ->
            if (bookDetail != null) {
                binding.tvBookTitleDetail.text = bookDetail.book?.title
                binding.tvBookTitleValue.text = bookDetail.book?.title
                binding.tvIsbnValue.text = bookDetail.book?.isbn.toString()
                binding.tvAuthorsDetail.text = bookDetail.book?.authors?.joinToString(", ")
                binding.tvAuthorsValue.text = bookDetail.book?.authors?.joinToString(", ")
                binding.tvPublishedDateDetail.text = getString(
                    R.string.published_at,
                    Formatter.convertDateFirebaseToDisplay(bookDetail.book?.publishedDate)
                )
                binding.tvGenreValue.text = bookDetail.book?.genre
                binding.tvPrintPriceValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(bookDetail.book?.printPrice))
                binding.tvSellPriceValue.text = getString(
                    R.string.rp_price,
                    Formatter.addThousandSeparatorTextView(bookDetail.book?.sellPrice)
                )
                binding.tvStockQtyValue.text = bookDetail.book?.stockQty.toString()
                if (bookDetail.book?.isPerpetual == true) {
                    binding.tvContractValue.text = getString(R.string.forever_contract)
                } else {
                    binding.tvContractValue.text = getString(R.string.contract_date_placeholder, Formatter.convertDateFirebaseToDisplay(bookDetail.book?.startContractDate), Formatter.convertDateFirebaseToDisplay(bookDetail.book?.endContractDate))
                }
                binding.userCard.tvUserAction.text = getString(R.string.created_by)
                binding.userCard.tvUserName.text = getString(R.string.by_at, bookDetail.logs?.createdBy, Formatter.convertUTCToLocal(bookDetail.logs?.createdAt))
            } else {
                // Handle null case, for example:
                // Clear UI fields or show a message indicating that the book was deleted
                binding.tvBookTitleDetail.text = ""
                // Clear other UI fields or show appropriate messages
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModel.deleteBookById(bookId) { success, message ->
            if (success) {
                Toast.makeText(this, "Book deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add book: $message", Toast.LENGTH_SHORT).show()
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