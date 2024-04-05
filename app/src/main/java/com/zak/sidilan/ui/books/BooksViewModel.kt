package com.zak.sidilan.ui.books

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.ui.addbook.AddBookViewModel
import org.koin.dsl.module


val booksViewModelModule = module {
    factory { BooksViewModel(get()) }
}

class BooksViewModel(private val repository: BookRepository) : ViewModel() {

    private val _bookList = MutableLiveData<ArrayList<Book>>()
    val bookList: MutableLiveData<ArrayList<Book>> get() = _bookList

    fun getBooks() {
        repository.getAllBooks().observeForever { bookList ->
            _bookList.value = bookList
        }
    }
}

