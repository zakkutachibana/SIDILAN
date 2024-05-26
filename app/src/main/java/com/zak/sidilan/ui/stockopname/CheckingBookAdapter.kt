package com.zak.sidilan.ui.stockopname

import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.BookOpname
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
            updateCheckBoxState()
            updateStatusText()
        }

        private fun updateCheckBoxState() {
            when (adapterBinding.tvStatusValue.text) {
                "Belum Diperiksa" -> adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_UNCHECKED
                "Stok Sesuai" -> adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_CHECKED
                "Stok Tidak Sesuai" -> adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_INDETERMINATE
            }
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
                    adapterBinding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.safe_yellow))
                }
            }
        }

        private fun checkStock(bookDetail: BookDetail) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Periksa Stok")
                .setMessage("Apakah benar buku \"${bookDetail.book?.title}\" memiliki stok sebanyak \"${bookDetail.stock?.stockQty} buku\" ?")
                .setPositiveButton(context.resources.getString(R.string.appropriate)) { dialog, _ ->
                    adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_CHECKED
                    adapterBinding.chipStockFalse.visibility = View.GONE
                    adapterBinding.tvArrow.visibility = View.GONE
                    adapterBinding.tvReason.visibility = View.GONE
                    adapterBinding.tvStockQty.text = "Stok"
                    updateStatusText()
                    dialog.dismiss()
                }
                .setNegativeButton(context.resources.getString(R.string.inappropriate)) { dialog, _ ->
                    discrepancy(bookDetail)
                    dialog.dismiss()
                }
                .show()
        }
        private fun discrepancy(bookDetail: BookDetail) {
            val layout = LayoutInflater.from(context).inflate(R.layout.layout_discrepancy, null)
            val edActual = layout.findViewById<EditText>(R.id.ed_actual_stock)
            val edExpected = layout.findViewById<EditText>(R.id.ed_expected_stock)
            val edReason = layout.findViewById<EditText>(R.id.ed_reason)
            val edlActual = layout.findViewById<TextInputLayout>(R.id.edl_actual_stock)
            val edlReason = layout.findViewById<TextInputLayout>(R.id.edl_reason)
            edExpected.setText(bookDetail.stock?.stockQty.toString())
            val dialog = MaterialAlertDialogBuilder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle("Stok Tidak Sesuai")
                .setView(layout)
                .setIcon(R.drawable.ic_discrepancy)
                .setMessage("Saat ini buku \"${bookDetail.book?.title}\" seharusnya memiliki stok sebanyak \"${bookDetail.stock?.stockQty} buku\". Namun ada berapa stok fisik sesungguhnya?")
                .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(context.resources.getString(R.string.adjust), null)
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (edActual.text.isNotEmpty() && edReason.text.isNotEmpty()) {
                    val discrepancy = edActual.text.toString().toInt() - bookDetail.stock?.stockQty?.toInt()!!
                    adapterBinding.chipStockQty.text = edActual.text.toString()
                    adapterBinding.chipStockFalse.text = bookDetail.stock.stockQty.toString()
                    adapterBinding.chipStockFalse.paintFlags = adapterBinding.chipStockFalse.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    adapterBinding.tvStockQty.text = when {
                        discrepancy > 0 -> "Stok (+$discrepancy)"
                        else -> "Stok ($discrepancy)"
                    }
                    adapterBinding.tvReason.text = edReason.text.toString()

                    adapterBinding.chipStockFalse.visibility = View.VISIBLE
                    adapterBinding.tvArrow.visibility = View.VISIBLE
                    adapterBinding.tvReason.visibility = View.VISIBLE

                    adapterBinding.cbStatus.checkedState = MaterialCheckBox.STATE_INDETERMINATE
                    updateStatusText()
                    dialog.dismiss()
                } else {
                    when {
                        edActual.text.isEmpty() -> edlActual.error = " "
                        edReason.text.isEmpty() -> edlReason.error = " "
                        else -> {
                            edlReason.error = " "
                            edlActual.error = " "
                        }
                    }
                }
            }
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
