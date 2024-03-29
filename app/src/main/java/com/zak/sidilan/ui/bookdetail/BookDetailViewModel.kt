package com.zak.sidilan.ui.bookdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.repositories.BookRepository
import org.koin.dsl.module

val bookDetailViewModelModule = module {
    factory { BookDetailViewModel(get()) }
}
class BookDetailViewModel(private val repository: BookRepository) : ViewModel() {
    private val _bookDetail = MutableLiveData<Book?>()
    val bookDetail: MutableLiveData<Book?> get() = _bookDetail

    fun getBookDetailById(bookId: String) {
        _bookDetail.value = null // Optional: Clear previous value before loading new data
        repository.getBookDetailById(bookId).observeForever { book ->
            _bookDetail.value = book
        }
    }
}