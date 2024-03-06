package com.zak.sidilan.ui.books

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.sidilan.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {
    private lateinit var adapter: BooksAdapter
    private var _binding: FragmentBooksBinding? = null
    private val viewModel: BooksViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            setupViewModel()
            viewModel.getBooks() }, 300)
    }

    private fun setupViewModel() {
        viewModel.bookList.observe(viewLifecycleOwner) {
            adapter = BooksAdapter(requireActivity(), ArrayList())
            binding.rvBooks.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvBooks.adapter = adapter
            adapter.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}