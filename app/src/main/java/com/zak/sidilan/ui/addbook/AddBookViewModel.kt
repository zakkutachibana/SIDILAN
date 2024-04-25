package com.zak.sidilan.ui.addbook

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    fun saveBookToFirebase(
        isbn: Long,
        title: String,
        authors: List<String>,
        coverImage: Uri?,
        genre: String,
        publishedDate: String,
        printPrice: Long,
        sellPrice: Long,
        isPerpetual: Boolean,
        startContractDate: String?,
        endContractDate: String?,
        createdBy: String,
        callback: (Boolean, String?) -> Unit
    ) {
        repository.saveBookToFirebase(
            isbn, title, authors, coverImage, genre, publishedDate, printPrice, sellPrice,
            isPerpetual, startContractDate, endContractDate, createdBy
        ) { isSuccess, message ->
            callback(isSuccess, message)
        }
    }
    fun updateBookFirebase(
        bookId: String,
        isbn: Long,
        title: String,
        authors: List<String>,
        coverImage: Uri?,
        genre: String,
        publishedDate: String,
        printPrice: Long,
        sellPrice: Long,
        isPerpetual: Boolean,
        startContractDate: String?,
        endContractDate: String?,
        callback: (Boolean, String?) -> Unit
    ) {
        repository.updateBookFirebase(
            bookId, isbn, title, authors, coverImage, genre, publishedDate, printPrice, sellPrice,
            isPerpetual, startContractDate, endContractDate
        ) { isSuccess, message ->
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
        _bookDetail.value = null // Optional: Clear previous value before loading new data
        repository.getBookDetailById(bookId).observeForever { book ->
            _bookDetail.value = book
        }
    }
}