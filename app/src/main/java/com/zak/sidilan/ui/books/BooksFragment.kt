package com.zak.sidilan.ui.books

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
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
            intent.putExtra("isbn", bookDetail.book?.isbn.toString())
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
            if (isEmpty) {
                binding.bookEmpty.visibility = View.VISIBLE
            } else {
                binding.bookEmpty.visibility = View.GONE
            }
        }
    }
    private fun setupAction() {
        binding.drawerIcon.setOnClickListener {
            (requireActivity() as MainActivity).binding.drawerLayout.open()
        }
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDING) {
                viewModel.filterBooks("")
            }
        }
        
        binding.searchBar.setOnClickListener {
            binding.searchView.show()
        }

        binding.searchView.addTransitionListener { _, previousState, newState ->
            if (previousState == SearchView.TransitionState.HIDDEN && newState == SearchView.TransitionState.SHOWN) {
                binding.searchView.editText.requestFocus()
            }
        }

        binding.searchView.editText.setOnEditorActionListener { v, actionId, event ->
            val query = v.text.toString()
            if (query.isNotEmpty()) {
                viewModel.filterBooks(query)
            }
            true
        }

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString()
            viewModel.filterBooks(query)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerView.stopShimmer()
            binding.shimmerView.visibility = View.GONE
            binding.rvBooks.visibility = View.VISIBLE
            (requireActivity() as MainActivity).binding.fab.show() }, 500)
    }
}

