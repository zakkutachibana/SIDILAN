package com.zak.sidilan.ui.executivemenus

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.databinding.LayoutBookCardBinding
import com.zak.sidilan.databinding.LayoutTopTenBinding
import com.zak.sidilan.util.Formatter

class TopTenAdapter(
    private val context: Context,
    private val onClickListener: (BookDetail) -> Unit
) : ListAdapter<BookDetail, TopTenAdapter.BooksViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding = LayoutTopTenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book, position + 1) // Pass rank as position + 1
    }

    inner class BooksViewHolder(private val adapterBinding: LayoutTopTenBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.cardBook.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position))
                }
            }
        }

        fun bind(bookDetail: BookDetail, rank: Int) {
            adapterBinding.ivBookCover.load(bookDetail.book?.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.book_placeholder)
            }
            adapterBinding.tvRank.text = rank.toString() // Set rank here
            adapterBinding.tvBookTitle.text = bookDetail.book?.title
            adapterBinding.tvSoldQty.text = context.getString(R.string.total_book_sold_qty, bookDetail.stock?.soldQty.toString())
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