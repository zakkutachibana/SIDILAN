package com.zak.sidilan.ui.users

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentTopTenBinding
import com.zak.sidilan.databinding.FragmentWhitelistBinding
import com.zak.sidilan.util.FirstItemMarginDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhitelistFragment : Fragment() {
    private var _binding: FragmentWhitelistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WhitelistAdapter

    private val viewModel: UserManagementViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhitelistBinding.inflate(inflater, container, false)
        setupViewModel()
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = WhitelistAdapter(requireContext()) { whitelist ->
            val layout = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_whitelist, null)
            val edEmail = layout.findViewById<EditText>(R.id.ed_email)
            val edlEmail = layout.findViewById<TextInputLayout>(R.id.edl_email)
            val edPhone = layout.findViewById<EditText>(R.id.ed_phone_number)
            val edlPhone = layout.findViewById<TextInputLayout>(R.id.edl_phone_number)
            val edRole = layout.findViewById<EditText>(R.id.ed_role)
            val edlRole = layout.findViewById<TextInputLayout>(R.id.edl_role)
            edlEmail.isEnabled = false
            edEmail.setText(whitelist.email)
            edPhone.setText(whitelist.phoneNumber)
            edRole.setText(whitelist.role)
            val dialog = MaterialAlertDialogBuilder(
                requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle(R.string.update_whitelist)
                .setView(layout)
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.add_whitelist_action)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ya", null)
                .setNeutralButton("Hapus", null)
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                when {
                    edPhone.text.isEmpty() -> edlPhone.error = "Nomor Telepon tidak boleh kosong!"
                    edRole.text.isEmpty() -> edlRole.error = "Role tidak boleh kosong!"
                    else -> {
                        val email = edEmail.text.toString()
                        val role = edRole.text.toString()
                        val phoneNumber = edPhone.text.toString()
                        viewModel.updateWhitelist(email, role, phoneNumber) {
                            dialog.dismiss()
                        }
                    }
                }
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                dialog.dismiss()
                showConfirmDialog(whitelist.email.toString())
            }

        }
        val pixel = resources.getDimensionPixelOffset(R.dimen.first_item_margin)
        val decorator = FirstItemMarginDecoration(pixel)
        binding.rvWhitelist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWhitelist.adapter = adapter
        binding.rvWhitelist.itemAnimator = DefaultItemAnimator()
        binding.rvWhitelist.addItemDecoration(decorator)
    }

    private fun setupViewModel() {
        viewModel.getWhitelist()
        viewModel.whitelist.observe(viewLifecycleOwner) { whitelist ->
            adapter.submitList(whitelist)
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmDialog(email: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.title_log_out))
            .setMessage("Whitelist akan dihapus. Konfirmasi hapus?")
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                viewModel.deleteWhitelist(email) { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}