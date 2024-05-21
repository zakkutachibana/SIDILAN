package com.zak.sidilan.ui.books

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.databinding.LayoutBookCardBinding
import com.zak.sidilan.util.Formatter

class BooksAdapter(
    private val context: Context,
    private val onClickListener: (BookDetail) -> Unit
) : ListAdapter<BookDetail, BooksAdapter.BooksViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding = LayoutBookCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

    inner class BooksViewHolder(private val adapterBinding: LayoutBookCardBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.cardBook.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position))
                }
            }
        }

        fun bind(bookDetail: BookDetail) {
            adapterBinding.ivBookCover.load(bookDetail.book?.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.book_placeholder)
            }
            adapterBinding.tvBookTitle.text = bookDetail.book?.title
            adapterBinding.tvAuthorName.text = bookDetail.book?.authors?.joinToString(", ")
            adapterBinding.tvIsbn.text = bookDetail.book?.isbn.toString()
            adapterBinding.chipPrintPrice.text = Formatter.addThousandSeparatorTextView(bookDetail.book?.printPrice)
            adapterBinding.chipSellPrice.text = Formatter.addThousandSeparatorTextView(bookDetail.book?.sellPrice)
            adapterBinding.chipStockQty.text = Formatter.addThousandSeparatorTextView(bookDetail.stock?.stockQty)
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<BookDetail>() {
        override fun areItemsTheSame(oldItem: BookDetail, newItem: BookDetail): Boolean {
            return oldItem.book?.isbn == newItem.book?.isbn
        }

        override fun areContentsTheSame(oldItem: BookDetail, newItem: BookDetail): Boolean {
            return oldItem == newItem
        }
    }
}
