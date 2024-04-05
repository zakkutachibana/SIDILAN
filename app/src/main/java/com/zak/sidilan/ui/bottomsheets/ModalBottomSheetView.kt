package com.zak.sidilan.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.entities.VolumeInfo
import com.zak.sidilan.databinding.LayoutBottomSheetViewBinding
import com.zak.sidilan.util.Formatter

class ModalBottomSheetView(private val type: Number, private val book: GoogleBooksResponse?) : BottomSheetDialogFragment() {

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
                val volumeInfo = book?.items?.getOrNull(0)?.volumeInfo
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
                    listener?.onButtonClicked(volumeInfo)
                    dismiss()
                }
                dialog?.setOnDismissListener {
                    listener?.onDismissed()
                }
            }
            //Type 2: Search ISBN not found
            2 -> {

            }
        }

    }
    interface BottomSheetListener {
        fun onButtonClicked(volumeInfo: VolumeInfo?)
        fun onDismissed()
    }
    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}