package com.zak.sidilan.ui.trx.choosebook

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.databinding.LayoutChooseBookCardBinding
import com.zak.sidilan.util.Formatter

class ChooseBookAdapter(
    private val context: Context,
    private val onClickListener: (BookDetail) -> Unit
) : ListAdapter<BookDetail, ChooseBookAdapter.BooksViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding = LayoutChooseBookCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

    inner class BooksViewHolder(private val adapterBinding: LayoutChooseBookCardBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
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
            adapterBinding.tvBookAuthors.text = bookDetail.book?.authors?.joinToString(", ")
            adapterBinding.tvBookStock.text = context.getString(R.string.current_stock_is, Formatter.addThousandSeparatorTextView(bookDetail.stock?.stockQty))
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