package com.zak.sidilan.ui.executivemenus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentBookInTrxOtherBinding
import com.zak.sidilan.databinding.FragmentBooksBinding
import com.zak.sidilan.databinding.FragmentTopTenBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import com.zak.sidilan.ui.books.BooksAdapter
import com.zak.sidilan.ui.books.BooksViewModel
import com.zak.sidilan.util.FirstItemMarginDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopTenFragment : Fragment() {
    private lateinit var binding : FragmentTopTenBinding
    private lateinit var adapter: TopTenAdapter
    private val viewModel: BooksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopTenBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TopTenAdapter(requireContext()) { bookDetail ->
            val intent = Intent(requireActivity(), BookDetailActivity::class.java)
            intent.putExtra("isbn", bookDetail.book?.isbn.toString())
            requireActivity().startActivity(intent)
        }
        val pixel = resources.getDimensionPixelOffset(R.dimen.first_item_margin)
        val decorator = FirstItemMarginDecoration(pixel)
        binding.rvTopTen.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopTen.adapter = adapter
        binding.rvTopTen.itemAnimator = DefaultItemAnimator()
        binding.rvTopTen.addItemDecoration(decorator)
    }

    private fun setupViewModel() {
        viewModel.getBooks()
        viewModel.sortedBySold.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
        }
    }

}