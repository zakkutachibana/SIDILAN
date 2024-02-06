package com.zak.sidilan.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentProfileBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.auth.AuthActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupViewModel()
        setupAction()
        return binding.root
    }
    private fun setupViewModel() {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    private fun setupAction() {
        binding.btnLogOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.title_log_out))
                .setMessage(resources.getString(R.string.message_log_out))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.title_log_out)) { dialog, which ->
                    val intent = Intent(context, AuthActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .show()
        }
        }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}