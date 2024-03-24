package com.zak.sidilan.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.databinding.LayoutBottomSheetActionBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.scan.ScanActivity

class ModalBottomSheetAction(private val type: Number, private val book: Book?, private val attachedActivity: Activity) : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutBottomSheetActionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalGetImage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (type) {
            1 -> {
                binding.titleBottomSheet.text = getString(R.string.title_action)

                binding.item1.setOnClickListener {
                    val intent = Intent(context, ScanActivity::class.java)
                    intent.putExtra("type", 1)
                    startActivity(intent)
                    dismiss()
                }
                binding.item2.setOnClickListener {
                    val intent = Intent(context, AddBookActivity::class.java)
                    intent.putExtra("type", 0)
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
                    // Code to start AddBookActivity in update mode
                    val intent = Intent(context, AddBookActivity::class.java)
                    intent.putExtra("is_update_mode", true) // Set the update mode flag to true
                    intent.putExtra("book_id", book?.id) // Pass the ID of the book to be updated
                    startActivity(intent)
                    dismiss()
                }
                binding.item2.setOnClickListener {
                    val layout = LayoutInflater.from(context).inflate(R.layout.layout_confirm_delete, null)
                    val editText = layout.findViewById<EditText>(R.id.ed_title_confirm)
                    val editTextLayout = layout.findViewById<TextInputLayout>(R.id.edl_title_confirm)
                    val dialog = MaterialAlertDialogBuilder(requireActivity(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(resources.getString(R.string.title_delete_book))
                        .setView(layout)
                        .setIcon(R.drawable.ic_delete)
                        .setMessage(resources.getString(R.string.will_be_deleted, "${book?.title}"))
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Ya", null)
                        .show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val userInput = editText.text.toString()
                        if (userInput == book?.title) {
                            dialog.dismiss()
                            attachedActivity.finish()
                        } else {
                            editTextLayout.error = "Judul buku tidak sesuai!"
                        }
                    }
                    dismiss()
                }
            }
        }

    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}