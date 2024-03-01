package com.zak.sidilan.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.Book
import com.zak.sidilan.databinding.FragmentBooksBinding
import com.zak.sidilan.util.ModalBottomSheet

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var reference: DatabaseReference = database.reference.child("books")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)

        setupViewModel()
        setupAction()
        getBooks()

        return binding.root

    }

    private fun getBooks() {
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (eachBook in snapshot.children) {
                    val book = eachBook.getValue(Book::class.java)

                    if (book != null) {
                        println("id: ${book.id}")
                        println("isbn: ${book.isbn}")
                        println("title: ${book.title}")
                        println("authors: ${book.authors}")
                        println("genre: ${book.genre}")
                        println("publishedDate: ${book.publishedDate}")
                        println("printPrice: ${book.printPrice}")
                        println("sellPrice: ${book.sellPrice}")
                        println("isPerpetual: ${book.isPerpetual}")
                        println("startContractDate: ${book.startContractDate}")
                        println("endContractDate: ${book.endContractDate}")
                        println("createdBy: ${book.createdBy}")
                        println("*******************************************")

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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