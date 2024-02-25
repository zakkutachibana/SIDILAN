package com.zak.sidilan.ui.trx.bookin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zak.sidilan.databinding.FragmentBookInTrxPrintBinding

class BookInTrxPrintFragment : Fragment() {
    private var _binding: FragmentBookInTrxPrintBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookInTrxPrintBinding.inflate(inflater, container, false)

        setAction()

        return binding.root
    }

    private fun setAction() {
        binding.chooseBookCard.root.setOnClickListener {
            Toast.makeText(activity, "Reserved for Choosing Book", Toast.LENGTH_SHORT).show()
        }
    }
}