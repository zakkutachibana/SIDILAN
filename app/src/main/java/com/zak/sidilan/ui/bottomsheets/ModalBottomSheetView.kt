package com.zak.sidilan.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.Stock
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.entities.VolumeInfo
import com.zak.sidilan.databinding.LayoutBottomSheetViewBinding
import com.zak.sidilan.util.Formatter

class ModalBottomSheetView(private val type: Number, private val googleBook: GoogleBooksResponse?, private val createdBook: BookDetail?) : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutBottomSheetViewBinding
    private var listener: BottomSheetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (type) {
            //Type 1: Search ISBN (Add)
            1 -> {
                binding.edlBookQty.visibility = View.GONE

                val volumeInfo = googleBook?.items?.getOrNull(0)?.volumeInfo
                val title = volumeInfo?.title.toString()
                val subtitle = volumeInfo?.subtitle.toString()
                val fullTitle = if (subtitle != "null" && subtitle.isNotEmpty()) "$title: $subtitle" else title
                binding.tvTitleBookView.text = fullTitle
                binding.tvAuthorBookView.text = volumeInfo?.authors?.joinToString(", ")
                binding.tvPublishedDateView.text = getString(R.string.published_at,
                    Formatter.convertDateAPIToDisplay(volumeInfo?.publishedDate)
                )
                val imageUrlFromApi = volumeInfo?.imageLinks?.thumbnail
                val trimmedImageUrl = imageUrlFromApi?.substringBefore("&img=1") + "&img=1"
                binding.ivBookCoverView.load(trimmedImageUrl)
                binding.btnAddView.setOnClickListener {
                    listener?.onButtonClicked(volumeInfo, null, null)
                    dismiss()
                }
                dialog?.setOnDismissListener {
                    listener?.onDismissed()
                }
            }
            //Type 2: Search ISBN not found
            2 -> {
                binding.tvPublishedDateView.visibility = View.GONE
                binding.tvAuthorBookView.visibility = View.GONE
                binding.edlBookQty.visibility = View.GONE

                binding.tvTitleBookView.text = createdBook?.book?.isbn.toString()
                binding.tvConfirmation.text = "Buku tidak ditemukan di GoogleBooks. Lanjut tambahkan buku secara manual?"
                binding.ivBookCoverView.load(R.drawable.book_placeholder)

                binding.btnAddView.setOnClickListener {
                    listener?.onButtonClicked(null, createdBook?.book , null)
                    dismiss()
                }
                dialog?.setOnDismissListener {
                    listener?.onDismissed()
                }
            }
            //Type 3: Add Book Transaction
            3 -> {
                binding.tvTitleBookView.text = createdBook?.book?.title
                binding.tvTitleBookView.maxLines = 1
                binding.tvAuthorBookView.text = createdBook?.book?.authors?.joinToString(", ")
                binding.tvAuthorBookView.maxLines = 1
                binding.tvPublishedDateView.text = getString(R.string.current_stock_is, createdBook?.stock?.stockQty.toString())
                binding.ivBookCoverView.load(createdBook?.book?.coverUrl)

                binding.btnAddView.setOnClickListener {
                    val bookQty = binding.edBookQty.text.toString().toLong()

                    listener?.onButtonClicked(null, createdBook?.book, bookQty)
                    dismiss()
                }
                dialog?.setOnDismissListener {
                    listener?.onDismissed()
                }
            }
        }
    }

    interface BottomSheetListener {
        fun onButtonClicked(volumeInfo: VolumeInfo?, book: Book?, bookQty: Long?)
        fun onDismissed()
    }
    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}