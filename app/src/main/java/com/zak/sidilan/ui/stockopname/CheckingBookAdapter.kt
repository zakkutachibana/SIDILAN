package com.zak.sidilan.ui.stockopname

import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.databinding.LayoutStockOpnameBinding
import com.zak.sidilan.util.CheckBoxState

class CheckingBookAdapter(
    private val context: Context,
    private val onClickListener: (BookOpname, Int) -> Unit,
    ) : ListAdapter<BookOpname, CheckingBookAdapter.BooksViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding = LayoutStockOpnameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

    inner class BooksViewHolder(private val adapterBinding: LayoutStockOpnameBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.layoutItemOpname.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position), position)
                }
            }
        }

        fun bind(bookOpname: BookOpname) {
            adapterBinding.ivBookCover.load(bookOpname.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.book_placeholder)
            }
            adapterBinding.tvBookTitle.text = bookOpname.bookTitle
            adapterBinding.tvIsbn.text = bookOpname.isbn.toString()
            adapterBinding.chipStockQty.text = bookOpname.stockExpected?.toString()
            updateStatusText(bookOpname)
        }


        private fun updateStatusText(bookOpname: BookOpname) {
            when (bookOpname.isAppropriate) {
                null  -> {
                    adapterBinding.tvStatusValue.text = "Belum Diperiksa"
                    adapterBinding.cbStatus.setState(CheckBoxState.UNCHECKED)
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.safe_red))
                }
                true -> {
                    adapterBinding.tvStatusValue.text = "Stok Sesuai"
                    adapterBinding.cbStatus.setState(CheckBoxState.CHECKED)
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.safe_green))
                    checkedState(bookOpname)
                }
                false -> {
                    adapterBinding.tvStatusValue.text = "Stok Tidak Sesuai"
                    adapterBinding.cbStatus.setState(CheckBoxState.INDETERMINATE)
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.safe_yellow))
                    indeterminateState(bookOpname)
                }
            }
        }

        private fun checkedState(bookOpname: BookOpname) {
            adapterBinding.chipStockQty.text = bookOpname.stockExpected.toString()
            adapterBinding.chipStockFalse.visibility = View.GONE
            adapterBinding.tvArrow.visibility = View.GONE
            adapterBinding.tvReason.visibility = View.GONE
            adapterBinding.tvStockQty.text = "Stok"
        }

        private fun indeterminateState(bookOpname: BookOpname) {
            adapterBinding.chipStockQty.text = bookOpname.stockActual.toString()
            adapterBinding.chipStockFalse.text = bookOpname.stockExpected.toString()
            adapterBinding.tvStockQty.text = when {
                bookOpname.discrepancy!! > 0 -> "Stok (+${bookOpname.discrepancy})"
                else -> "Stok (${bookOpname.discrepancy})"
            }
            adapterBinding.tvReason.text = bookOpname.reason
            adapterBinding.chipStockFalse.paintFlags = adapterBinding.chipStockFalse.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            adapterBinding.chipStockFalse.visibility = View.VISIBLE
            adapterBinding.tvArrow.visibility = View.VISIBLE
            adapterBinding.tvReason.visibility = View.VISIBLE
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<BookOpname>() {
        override fun areItemsTheSame(oldItem: BookOpname, newItem: BookOpname): Boolean {
            return oldItem.isbn == newItem.isbn
        }

        override fun areContentsTheSame(oldItem: BookOpname, newItem: BookOpname): Boolean {
            return oldItem == newItem
        }
    }
}
