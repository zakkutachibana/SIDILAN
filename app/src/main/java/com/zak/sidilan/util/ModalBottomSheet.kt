package com.zak.sidilan.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zak.sidilan.R
import com.zak.sidilan.databinding.LayoutBottomSheetBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.addbook.AddBookByScanActivity

class ModalBottomSheet(private val type: Number) : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalGetImage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (type) {
            //Type 1: Add Books
            1 -> {
                binding.titleBottomSheet.text = getString(R.string.title_action)

                binding.item1.setOnClickListener {
                    val intent = Intent(context, AddBookByScanActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }
                binding.item2.setOnClickListener {
                    val intent = Intent(context, AddBookActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }
            }
            //Type 2: Edit etc...
            2 -> {
                binding.item1.setOnClickListener {
                    Toast.makeText(context, "yuh", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                binding.item2.setOnClickListener {
                    Toast.makeText(context, "yuh", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }

    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}