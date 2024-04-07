package com.zak.sidilan.ui.users

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
        adapter = WhitelistAdapter(requireContext()) {}
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
}