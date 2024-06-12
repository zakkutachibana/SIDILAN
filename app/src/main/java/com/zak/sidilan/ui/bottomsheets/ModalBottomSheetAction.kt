package com.zak.sidilan.ui.bottomsheets

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.databinding.LayoutBottomSheetActionBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.bookdetail.BookDetailViewModel
import com.zak.sidilan.ui.scan.ScanActivity
import com.zak.sidilan.ui.users.UserManagementViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class ModalBottomSheetAction(
    private val type: Number,
    private val bookDetail: BookDetail?,
    private val attachedActivity: Activity,
    private val email: String?
) : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutBottomSheetActionBinding
    private val bookDetailViewModel: BookDetailViewModel by viewModel()
    private val userManagementViewModel: UserManagementViewModel by viewModel()

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
                    val intent = Intent(context, AddBookActivity::class.java)
                    intent.putExtra("is_update_mode", true)
                    intent.putExtra("isbn", bookDetail?.book?.isbn.toString())
                    Log.d("BOTTOM SHEET", bookDetail?.book?.isbn.toString())
                    startActivity(intent)
                    dismiss()
                }
                binding.item2.setOnClickListener {
                    val layout =
                        LayoutInflater.from(context).inflate(R.layout.layout_confirm_delete, null)
                    val editText = layout.findViewById<EditText>(R.id.ed_title_confirm)
                    val editTextLayout =
                        layout.findViewById<TextInputLayout>(R.id.edl_title_confirm)
                    val dialog = MaterialAlertDialogBuilder(
                        requireActivity(),
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
                    )
                        .setTitle(resources.getString(R.string.title_delete_book))
                        .setView(layout)
                        .setIcon(R.drawable.ic_delete)
                        .setMessage(
                            resources.getString(
                                R.string.will_be_deleted,
                                "${bookDetail?.book?.title}"
                            )
                        )
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Ya", null)
                        .show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val userInput = editText.text.toString()
                        if (userInput == bookDetail?.book?.title) {
                            bookDetailViewModel.deleteBookById(bookDetail.book.isbn.toString()) { isSuccess, message ->
                                if (isSuccess) {
                                    MotionToast.createColorToast(
                                        requireActivity(),
                                        "Success",
                                        "Buku berhasil dihapus",
                                        MotionToastStyle.DELETE,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(
                                            requireContext(),
                                            www.sanju.motiontoast.R.font.helvetica_regular
                                        )
                                    )
                                    dialog.dismiss()
                                    attachedActivity.finish()
                                } else {
                                    MotionToast.createColorToast(
                                        requireActivity(),
                                        "Success",
                                        "Buku gagal dihapus: $message",
                                        MotionToastStyle.DELETE,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(
                                            requireContext(),
                                            www.sanju.motiontoast.R.font.helvetica_regular
                                        )
                                    )
                                }
                            }
                        } else {
                            editTextLayout.error = "Judul buku tidak sesuai!"
                        }
                    }
                }
            }

            3 -> {
                binding.titleBottomSheet.text = getString(R.string.title_action)
                binding.tvItem1.text = "Ubah Role Pengguna"
                binding.ivItem1.setImageResource(R.drawable.ic_edit_user)
                binding.tvItem2.text = "Hapus Akses Pengguna"
                binding.ivItem2.setImageResource(R.drawable.ic_delete)

                binding.item1.setOnClickListener {
                    val layout =
                        LayoutInflater.from(context).inflate(R.layout.layout_update_role, null)
                    val editText = layout.findViewById<EditText>(R.id.ed_role)
                    val editTextLayout = layout.findViewById<TextInputLayout>(R.id.edl_role)
                    val changeRoleDialog = MaterialAlertDialogBuilder(
                        requireActivity(),
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
                    )
                        .setTitle("Ubah Role")
                        .setView(layout)
                        .setIcon(R.drawable.ic_edit_user)
                        .setMessage("Pilih role baru untuk akun ini.")
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            dialog.dismiss()
                            dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.yes), null)
                        .show()
                    changeRoleDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (editText.text.isNotEmpty()) {
                            val newRole = editText.text.toString()
                            userManagementViewModel.updateRole(email.toString(), newRole) {
                                MotionToast.createColorToast(
                                    requireActivity(),
                                    "Info",
                                    it,
                                    MotionToastStyle.INFO,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(
                                        requireContext(),
                                        www.sanju.motiontoast.R.font.helvetica_regular
                                    )
                                )
                                changeRoleDialog.dismiss()
                                attachedActivity.finish()
                            }
                        } else {
                            editTextLayout.error = "Masukkan Role"
                        }

                    }
                }
                binding.item2.setOnClickListener {
                    val deleteDialog = MaterialAlertDialogBuilder(requireActivity())
                        .setTitle("Hapus Akses")
                        .setMessage("Akses dari pengguna ini akan dihapus. Konfirmasi hapus?")
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            dialog.dismiss()
                            dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.yes), null)
                        .show()
                    deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                        userManagementViewModel.deleteWhitelist(email.toString()) {
                            MotionToast.createColorToast(
                                requireActivity(),
                                "Delete",
                                it,
                                MotionToastStyle.DELETE,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    www.sanju.motiontoast.R.font.helvetica_regular
                                )
                            )
                            deleteDialog.dismiss()
                            dismiss()
                            attachedActivity.finish()
                        }
                    }
                }
            }
        }

    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}