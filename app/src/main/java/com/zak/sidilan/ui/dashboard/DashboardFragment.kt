package com.zak.sidilan.ui.dashboard

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setView()
        setViewModel()
        setAction()
        return binding.root
    }

    private fun setView() {
        binding.userCard.tvUserAction.text = "Surya Widjojo"
        binding.userCard.tvUserName.text = "Administrator"
    }

    private fun setViewModel() {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    private fun setAction() {
        binding.btnExpand.setOnClickListener {
            val expVis = if (binding.clExpand.visibility == View.GONE) {
                binding.btnExpand.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_up, null)
                View.VISIBLE
            } else {
                binding.btnExpand.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_down, null)
                View.GONE
            }
            binding.clExpand.visibility = expVis
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}