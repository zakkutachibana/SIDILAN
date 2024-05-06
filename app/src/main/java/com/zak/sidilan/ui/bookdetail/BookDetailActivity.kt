package com.zak.sidilan.ui.bookdetail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityBookDetailBinding
import com.zak.sidilan.util.Formatter
import com.zak.sidilan.ui.bottomsheets.ModalBottomSheetAction
import com.zak.sidilan.ui.users.UserDetailActivity
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
        }
        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupAction() {
        binding.btnEditDelete.setOnClickListener { it ->
            if (it != null) {
                viewModel.bookDetail.observe(this) {
                    val modalBottomSheetAction = ModalBottomSheetAction(2, it, this)
                    modalBottomSheetAction.show(supportFragmentManager, ModalBottomSheetAction.TAG)
                }
            }
        }
        binding.userCard.cardUser.setOnClickListener {
            viewModel.user.observe(this) { user ->
                val intent = Intent(this, UserDetailActivity::class.java)
                intent.putExtra("userId", user?.id)
                startActivity(intent)
            }
        }
    }
    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Buku"
        binding.userCard.tvUserAction.text = getString(R.string.created_by)
    }

    private fun setupViewModel() {
        viewModel.bookDetail.observe(this) { bookDetail ->
            if (bookDetail != null) {
                binding.ivBookCover.load(bookDetail.book?.coverUrl)
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
                binding.tvStockQtyValue.text = bookDetail.stock?.stockQty.toString()
                if (bookDetail.book?.isPerpetual == true) {
                    binding.tvContractValue.text = getString(R.string.forever_contract)
                } else {
                    binding.tvContractValue.text = getString(R.string.contract_date_placeholder, Formatter.convertDateFirebaseToDisplay(bookDetail.book?.startContractDate), Formatter.convertDateFirebaseToDisplay(bookDetail.book?.endContractDate))
                }
                viewModel.getUserById(bookDetail.logs?.createdBy.toString())
                viewModel.user.observe(this) { user ->
                    binding.userCard.tvUserName.text = getString(R.string.by_at, user?.displayName, user?.role, Formatter.convertEpochToLocal(bookDetail.logs?.createdAt))
                    binding.userCard.ivProfilePicture.load(user?.photoUrl)
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