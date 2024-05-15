package com.zak.sidilan.ui.books

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.sidilan.databinding.FragmentBooksBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val booksFragmentModule = module {
    factory { BooksFragment() }
}

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BooksViewModel by viewModel()

    private lateinit var adapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        binding.shimmerView.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            setupRecyclerView()
            setupViewModel() }, 300)

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = BooksAdapter(requireContext()) { bookDetail ->
            val intent = Intent(requireActivity(), BookDetailActivity::class.java)
            intent.putExtra("bookId", bookDetail.book?.id)
            requireActivity().startActivity(intent)
        }
        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = adapter
        binding.rvBooks.itemAnimator = DefaultItemAnimator()
    }

    private fun setupViewModel() {
        viewModel.getBooks()
        viewModel.bookList.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE }, 1000)
        }
        viewModel.isBookListEmpty.observe(viewLifecycleOwner) { isEmpty ->

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
