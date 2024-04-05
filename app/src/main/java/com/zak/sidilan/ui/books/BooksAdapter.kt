package com.zak.sidilan.ui.books

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.databinding.LayoutBookCardBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import com.zak.sidilan.util.Formatter

class BooksAdapter(
    private val context: Context,
    private val onClickListener: (Book) -> Unit
) : ListAdapter<Book, BooksAdapter.BooksViewHolder>(BookDiffCallback()) {

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

        fun bind(book: Book) {
            adapterBinding.tvBookTitle.text = book.title
            adapterBinding.tvAuthorName.text = book.authors.joinToString(", ")
            adapterBinding.tvIsbn.text = book.isbn.toString()
            adapterBinding.chipPrintPrice.text = Formatter.addThousandSeparatorTextView(book.printPrice)
            adapterBinding.chipSellPrice.text = Formatter.addThousandSeparatorTextView(book.sellPrice)
            adapterBinding.chipStockQty.text = Formatter.addThousandSeparatorTextView(book.stockQty!!)
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
