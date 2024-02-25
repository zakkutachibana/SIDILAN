package com.zak.sidilan.ui.trx.bookout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentBookOutTrxOtherBinding
import com.zak.sidilan.databinding.FragmentBookOutTrxSellBinding

class BookOutTrxSellFragment : Fragment() {

    private var _binding: FragmentBookOutTrxSellBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookOutTrxSellBinding.inflate(inflater, container, false)
        return binding.root
    }

}