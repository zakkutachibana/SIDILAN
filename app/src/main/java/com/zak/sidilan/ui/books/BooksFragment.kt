package com.zak.sidilan.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zak.sidilan.databinding.FragmentBooksBinding
import com.zak.sidilan.util.ModalBottomSheet

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        setupAction()

        return root

    }

    private fun setupViewModel() {
        val booksViewModel = ViewModelProvider(this)[BooksViewModel::class.java]
        booksViewModel.text.observe(viewLifecycleOwner) {
            binding.textBooks.text = it
        }
    }

    private fun setupAction() {
        binding.floatingActionButton.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet(1)
            modalBottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}