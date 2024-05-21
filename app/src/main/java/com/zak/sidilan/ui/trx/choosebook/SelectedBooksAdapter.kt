package com.zak.sidilan.ui.trx.choosebook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.databinding.LayoutBookHorizontalBinding
import com.zak.sidilan.ui.trx.BookTrxViewModel
import com.zak.sidilan.util.Formatter

class SelectedBooksAdapter(
    private val type: Int,
    private val context: Context,
    private val viewModel: BookTrxViewModel, // Pass your ViewModel here
    private val onClickListener: (BookQtyPrice) -> Unit,
) : RecyclerView.Adapter<SelectedBooksAdapter.BooksViewHolder>() {

    private val booksList = mutableListOf<BookQtyPrice>()

    fun updateBooks(newBooks: List<BookQtyPrice>) {
        booksList.clear()
        booksList.addAll(newBooks)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding =
            LayoutBookHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val bookPrice = booksList[position] // Access book directly from the list
        holder.bind(bookPrice)
    }

    override fun getItemCount(): Int {
        return booksList.size // Return the size of the list
    }

    inner class BooksViewHolder(private val adapterBinding: LayoutBookHorizontalBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {

        init {
            adapterBinding.cardCoverItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(booksList[position]) // Pass book directly from the list
                }
            }

            adapterBinding.btnIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val removedBook = booksList.removeAt(position)
                    viewModel.removeBook(removedBook)
                    notifyItemRemoved(position)
                }
            }
        }

        fun bind(bookQtyPrice: BookQtyPrice) {
            val subtotal = bookQtyPrice.bookQty.times(bookQtyPrice.book.printPrice)
            adapterBinding.ivBookCover.load(bookQtyPrice.book.coverUrl)
            adapterBinding.tvBookTitleItem.text = bookQtyPrice.book.title
            adapterBinding.tvBookQty.text = context.getString(R.string.total_stock_qty, bookQtyPrice.bookQty.toString())
            when (type) {
                1 -> {
                    adapterBinding.tvBookPrice.text = context.getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(subtotal))
                }
                2 -> {
                    adapterBinding.tvBookPrice.visibility = View.GONE
                }
                3 -> {
                    adapterBinding.tvBookPrice.text = context.getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(subtotal))
                }
            }
        }
    }

}

