package com.zak.sidilan.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zak.sidilan.R
import com.zak.sidilan.databinding.LayoutBottomSheetBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.addbook.AddBookByScanActivity
import com.zak.sidilan.ui.auth.AuthActivity

class ModalBottomSheet(private val type: Number, private val detail: String?) : BottomSheetDialogFragment() {

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
                binding.titleBottomSheet.text = getString(R.string.title_action)
                binding.tvItem1.text = "Ubah Detail Buku"
                binding.ivItem1.setImageResource(R.drawable.ic_book_edit)
                binding.tvItem2.text = "Hapus Buku"
                binding.ivItem2.setImageResource(R.drawable.ic_delete)
                binding.item1.setOnClickListener {
                    Toast.makeText(context, "Reserved for Edit", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                binding.item2.setOnClickListener {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(resources.getString(R.string.title_log_out))
                        .setMessage(resources.getString(R.string.will_be_deleted, "$detail"))
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            dialog.dismiss()
                            dismiss()
                        }
                        .setPositiveButton("Ya") { dialog, which ->
                            Toast.makeText(context, "Reserved for Delete", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            dismiss()
                        }
                        .show()
                }
            }
        }

    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}