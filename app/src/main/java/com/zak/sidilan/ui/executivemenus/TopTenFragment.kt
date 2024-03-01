package com.zak.sidilan.ui.executivemenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentBookInTrxOtherBinding
import com.zak.sidilan.databinding.FragmentTopTenBinding

class TopTenFragment : Fragment() {
    private var _binding: FragmentTopTenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopTenBinding.inflate(inflater, container, false)
        return binding.root
    }

}