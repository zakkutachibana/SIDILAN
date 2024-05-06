package com.zak.sidilan.ui.trxdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.databinding.LayoutTrxHistoryItemBinding
import com.zak.sidilan.ui.bookdetail.BookDetailViewModel
import com.zak.sidilan.util.Formatter

class BookTrxHistoryAdapter(
    private val context: Context,
    private val viewModel: BookDetailViewModel,
    private val type: Int,
) : ListAdapter<BookSubtotal, BookTrxHistoryAdapter.UsersViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = LayoutTrxHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val bookSubtotal = getItem(position)
        holder.bind(bookSubtotal)
    }

    inner class UsersViewHolder(private val adapterBinding: LayoutTrxHistoryItemBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        fun bind(bookSubtotal: BookSubtotal) {
            viewModel.getBookDetailByIdCallback(bookSubtotal.bookId) { book ->
                adapterBinding.tvBookTitleTrx.text = book?.title
                adapterBinding.tvBookQty.text = bookSubtotal.qty.toString()
                adapterBinding.tvBookSubtotal.text = Formatter.addThousandSeparatorTextView(bookSubtotal.subtotalPrice)
                when (type) {
                    //Type 1 : Print Price
                    1 -> {
                        adapterBinding.tvBookPrice.text = context.getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(book?.printPrice))
                    }
                    //Type 2 : Sell Price
                    2 -> {
                        adapterBinding.tvBookPrice.text = context.getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(book?.sellPrice))
                    }
                    else -> {
                        adapterBinding.tvBookSubtotal.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        adapterBinding.tvBookPrice.text = "-"
                        adapterBinding.tvBookSubtotal.text = "-"
                        adapterBinding.tvBookSubtotalRp.visibility = View.GONE

                    }
                }
            }

        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<BookSubtotal>() {
        override fun areItemsTheSame(oldItem: BookSubtotal, newItem: BookSubtotal): Boolean {
            return oldItem.bookId== newItem.bookId
        }

        override fun areContentsTheSame(oldItem: BookSubtotal, newItem: BookSubtotal): Boolean {
            return oldItem == newItem
        }
    }
}
