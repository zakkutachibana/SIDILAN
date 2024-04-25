package com.zak.sidilan.ui.trx.choosebook

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookPrice
import com.zak.sidilan.databinding.LayoutBookHorizontalBinding
import com.zak.sidilan.ui.trx.bookin.BookInTrxViewModel
import com.zak.sidilan.util.Formatter

class SelectedBooksAdapter(
    private val context: Context,
    private val viewModel: BookInTrxViewModel, // Pass your ViewModel here
    private val onClickListener: (BookPrice) -> Unit,
) : RecyclerView.Adapter<SelectedBooksAdapter.BooksViewHolder>() {

    private val booksList = mutableListOf<BookPrice>()

    fun updateBooks(newBooks: List<BookPrice>) {
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

        fun bind(bookPrice: BookPrice) {
            val subtotal = bookPrice.bookQty?.times(bookPrice.book?.printPrice!!)
            adapterBinding.ivBookCover.load(bookPrice.book?.coverUrl)
            adapterBinding.tvBookTitleItem.text = bookPrice.book?.title
            adapterBinding.tvBookQty.text = context.getString(R.string.total_stock_qty, bookPrice.bookQty.toString())
            adapterBinding.tvBookPrice.text = context.getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(subtotal))
        }
    }
}
