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
import com.zak.sidilan.MainActivity
import com.zak.sidilan.databinding.FragmentBooksBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val booksFragmentModule = module {
    factory { BooksFragment() }
}

class BooksFragment : Fragment() {
    private lateinit var binding : FragmentBooksBinding
    private val viewModel: BooksViewModel by viewModel()

    private lateinit var adapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmerView.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            setupRecyclerView()
            setupViewModel()
            setupAction()}, 300)
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
        }
        viewModel.isBookListEmpty.observe(viewLifecycleOwner) { isEmpty ->

        }
    }
    private fun setupAction() {
        binding.searchBar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).binding.drawerLayout.open()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerView.stopShimmer()
            binding.shimmerView.visibility = View.GONE
            binding.rvBooks.visibility = View.VISIBLE
            (requireActivity() as MainActivity).binding.fab.show() }, 500)


    }

}
