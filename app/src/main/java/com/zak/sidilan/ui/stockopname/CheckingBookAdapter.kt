package com.zak.sidilan.ui.stockopname

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.databinding.LayoutStockOpnameBinding

class CheckingBookAdapter(
    private val context: Context,
) : ListAdapter<BookDetail, CheckingBookAdapter.BooksViewHolder>(BookDiffCallback()) {

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
                    checkStock(getItem(position))
                }
            }
            adapterBinding.cbStatus.setOnCheckedChangeListener { _, _ ->
                updateStatusText()
            }
        }

        fun bind(bookDetail: BookDetail) {
            adapterBinding.ivBookCover.load(bookDetail.book?.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.book_placeholder)
            }
            adapterBinding.tvBookTitle.text = bookDetail.book?.title
            adapterBinding.tvIsbn.text = bookDetail.book?.isbn.toString()
            adapterBinding.chipStockQty.text = bookDetail.stock?.stockQty.toString()
            updateStatusText()
        }

        private fun updateStatusText() {
            when (adapterBinding.cbStatus.checkedState) {
                MaterialCheckBox.STATE_UNCHECKED -> {
                    adapterBinding.tvStatusValue.text = "Belum Diperiksa"
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.safe_red))
                }
                MaterialCheckBox.STATE_CHECKED -> {
                    adapterBinding.tvStatusValue.text = "Stok Sesuai"
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.safe_green))
                }
                MaterialCheckBox.STATE_INDETERMINATE -> {
                    adapterBinding.tvStatusValue.text = "Stok Tidak Sesuai"
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                }
            }
        }
        private fun checkStock(bookDetail: BookDetail) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Periksa Stok")
                .setMessage("Apakah benar buku ${bookDetail.book?.title}  ${bookDetail.stock?.stockQty.toString()}")
                .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, which ->
                    adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_CHECKED
                    dialog.dismiss()
                }
                .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, which ->
                    adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_INDETERMINATE
                    dialog.dismiss()
                }

                .show()
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