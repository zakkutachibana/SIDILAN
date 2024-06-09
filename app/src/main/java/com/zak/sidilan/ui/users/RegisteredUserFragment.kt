package com.zak.sidilan.ui.users

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentRegisteredUserBinding
import com.zak.sidilan.util.FirstItemMarginDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class RegisteredUserFragment : Fragment() {
    private var _binding: FragmentRegisteredUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UsersAdapter
    private val viewModel: UserManagementViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisteredUserBinding.inflate(inflater, container, false)
        setupViewModel()
        setupRecyclerView()
        setupAction()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(requireContext(), viewModel) { user ->
            val intent = Intent(requireContext(), UserDetailActivity::class.java)
            intent.putExtra("userId", user.id)
            startActivity(intent)
        }
        val pixel = resources.getDimensionPixelOffset(R.dimen.first_item_margin)
        val decorator = FirstItemMarginDecoration(pixel)
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
        binding.rvUsers.itemAnimator = DefaultItemAnimator()
        binding.rvUsers.addItemDecoration(decorator)
    }

    private fun setupViewModel() {
        viewModel.getUsers()
        viewModel.userList.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            MotionToast.createColorToast(requireActivity(),
                "Info",
                message,
                MotionToastStyle.INFO,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
        }
    }

    private fun setupAction() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, _ ->
            val selectedRoles = mutableListOf<String>()
            for (chipId in group.checkedChipIds) {
                val chip = group.findViewById<Chip>(chipId)
                selectedRoles.add(chip.text.toString())
            }
            viewModel.filterUsersByRoles(selectedRoles)
        }
    }
}