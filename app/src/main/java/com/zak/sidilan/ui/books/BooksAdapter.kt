package com.zak.sidilan.ui.books

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zak.sidilan.data.Book
import com.zak.sidilan.databinding.LayoutBookCardBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import com.zak.sidilan.util.Formatter

class BooksAdapter(var context: Context, private var bookList: ArrayList<Book>)
    : RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

    inner class BooksViewHolder(val adapterBinding: LayoutBookCardBinding)
        : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding = LayoutBookCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
    fun updateData(newBooks: List<Book>) {
        bookList.clear()
        bookList.addAll(newBooks)
    }
    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        holder.adapterBinding.tvBookTitle.text = bookList[position].title
        holder.adapterBinding.tvAuthorName.text = bookList[position].authors.joinToString(", ")
        holder.adapterBinding.tvIsbn.text = bookList[position].isbn.toString()
        holder.adapterBinding.chipPrintPrice.text = Formatter.addThousandSeparatorTextView(bookList[position].printPrice)
        holder.adapterBinding.chipSellPrice.text = Formatter.addThousandSeparatorTextView(bookList[position].sellPrice)

        holder.adapterBinding.cardBook.setOnClickListener{
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("bookId", bookList[position].id)
            context.startActivity(intent)
        }
    }

}