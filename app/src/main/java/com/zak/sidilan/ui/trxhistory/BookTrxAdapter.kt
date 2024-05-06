package com.zak.sidilan.ui.trxhistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookInDonationTrx
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookOutDonationTrx
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.entities.BookTrxDetail
import com.zak.sidilan.databinding.LayoutTrxHistoryCardBinding
import com.zak.sidilan.util.Formatter

class BookTrxAdapter(
    private val context: Context,
    private val onClickListener: (BookTrxDetail) -> Unit
) : ListAdapter<BookTrxDetail, BookTrxAdapter.UsersViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = LayoutTrxHistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val bookTrxDetail = getItem(position)
        holder.bind(bookTrxDetail)
    }

    inner class UsersViewHolder(private val adapterBinding: LayoutTrxHistoryCardBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.cardTrx.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position))
                }
            }
        }

        fun bind(bookTrxDetail: BookTrxDetail) {
            when (bookTrxDetail.bookTrx) {
                is BookInPrintingTrx -> {
                    adapterBinding.ivIcon.load(R.drawable.img_printing)
                    adapterBinding.tvTitleTrx.text = context.getString(R.string.title_book_in_print)
                    adapterBinding.tvTrxDate.text = Formatter.convertDateFirebaseToDisplay(bookTrxDetail.bookTrx.bookInDate)
                    adapterBinding.tvSource.text = bookTrxDetail.bookTrx.printingShopName
                    adapterBinding.tvTrxMoney.text = context.getString(R.string.rp_price_minus, Formatter.addThousandSeparatorTextView(bookTrxDetail.bookTrx.finalCost))
                    adapterBinding.tvTrxMoney.setTextColor(ContextCompat.getColor(context, R.color.safe_red))
                    adapterBinding.tvTotalQty.text = context.getString(R.string.plus_qty, bookTrxDetail.bookTrx.totalBookQty.toString())
                    adapterBinding.tvTotalQty.setTextColor(ContextCompat.getColor(context, R.color.safe_green))
                    adapterBinding.tvBookKind.text = context.getString(R.string.book_count, bookTrxDetail.bookTrx.totalBookKind.toString())
                }
                is BookInDonationTrx -> {
                    adapterBinding.ivIcon.load(R.drawable.img_receive_donation)
                    adapterBinding.tvTitleTrx.text = context.getString(R.string.title_book_in_other)
                    adapterBinding.tvTrxDate.text = Formatter.convertDateFirebaseToDisplay(bookTrxDetail.bookTrx.bookInDate)
                    adapterBinding.tvSource.text = bookTrxDetail.bookTrx.donorName
                    adapterBinding.tvTrxMoney.visibility = View.GONE
                    adapterBinding.tvTotalQty.text = context.getString(R.string.plus_qty, bookTrxDetail.bookTrx.totalBookQty.toString())
                    adapterBinding.tvTotalQty.setTextColor(ContextCompat.getColor(context, R.color.safe_green))
                    adapterBinding.tvBookKind.text = context.getString(R.string.book_count, bookTrxDetail.bookTrx.totalBookKind.toString())
                }
                is BookOutSellingTrx -> {
                    adapterBinding.ivIcon.load(R.drawable.img_selling)
                    adapterBinding.tvTitleTrx.text = context.getString(R.string.title_book_out_selling, bookTrxDetail.bookTrx.sellingPlatform)
                    adapterBinding.tvTrxDate.text = Formatter.convertDateFirebaseToDisplay(bookTrxDetail.bookTrx.bookOutDate)
                    adapterBinding.tvSource.text = bookTrxDetail.bookTrx.buyerName
                    adapterBinding.tvTrxMoney.text = context.getString(R.string.rp_price_plus, Formatter.addThousandSeparatorTextView(bookTrxDetail.bookTrx.finalPrice))
                    adapterBinding.tvTrxMoney.setTextColor(ContextCompat.getColor(context, R.color.safe_green))
                    adapterBinding.tvTotalQty.text = context.getString(R.string.minus_qty, bookTrxDetail.bookTrx.totalBookQty.toString())
                    adapterBinding.tvTotalQty.setTextColor(ContextCompat.getColor(context, R.color.safe_red))
                    adapterBinding.tvBookKind.text = context.getString(R.string.book_count, bookTrxDetail.bookTrx.totalBookKind.toString())
                }
                is BookOutDonationTrx -> {
                    adapterBinding.ivIcon.load(R.drawable.img_donating)
                    adapterBinding.tvTitleTrx.text = context.getString(R.string.title_book_out_other)
                    adapterBinding.tvTrxDate.text = Formatter.convertDateFirebaseToDisplay(bookTrxDetail.bookTrx.bookOutDate)
                    adapterBinding.tvSource.text = bookTrxDetail.bookTrx.doneeName
                    adapterBinding.tvTrxMoney.visibility = View.GONE
                    adapterBinding.tvTotalQty.text = context.getString(R.string.minus_qty, bookTrxDetail.bookTrx.totalBookQty.toString())
                    adapterBinding.tvTotalQty.setTextColor(ContextCompat.getColor(context, R.color.safe_red))
                    adapterBinding.tvBookKind.text = context.getString(R.string.book_count, bookTrxDetail.bookTrx.totalBookKind.toString())
                }

                else -> {}
            }

        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<BookTrxDetail>() {
        override fun areItemsTheSame(oldItem: BookTrxDetail, newItem: BookTrxDetail): Boolean {
            return oldItem.bookTrx?.id == newItem.bookTrx?.id
        }

        override fun areContentsTheSame(oldItem: BookTrxDetail, newItem: BookTrxDetail): Boolean {
            return oldItem == newItem
        }
    }
}
