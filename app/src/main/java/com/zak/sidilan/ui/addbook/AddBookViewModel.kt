package com.zak.sidilan.ui.addbook

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.util.SingleLiveEvent
import org.koin.dsl.module

val addBookViewModelModule = module {
    factory { AddBookViewModel(get()) }
}
class AddBookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _bookByIsbn = SingleLiveEvent<GoogleBooksResponse>()
    val bookByIsbn: LiveData<GoogleBooksResponse>
        get() = _bookByIsbn

    private val _bookDetail = MutableLiveData<BookDetail?>()
    val bookDetail: MutableLiveData<BookDetail?> get() = _bookDetail
    fun saveBookToFirebase(book: Book, createdBy: String, callback: (Boolean, String?) -> Unit) {
        repository.saveBookToFirebase(book, createdBy) { isSuccess, message ->
            callback(isSuccess, message)
        }
    }
    fun updateBookFirebase(book: Book, oldCover: String,  callback: (Boolean, String?) -> Unit) {
        repository.updateBookFirebase(book, oldCover) { isSuccess, message ->
            callback(isSuccess, message)
        }
    }

    fun searchBookByISBN(isbn: String) {
        repository.searchBookByISBN(isbn) { response ->
            if (response.totalItems == 0) {
                _toastMessage.postValue("Book not found on Google Books!")
            } else {
                _bookByIsbn.postValue(response)
            }
        }
    }


    fun getBookDetailById(bookId: String) {
        _bookDetail.value = null
        repository.getBookDetailById(bookId).observeForever { book ->
            _bookDetail.value = book
        }
    }
}