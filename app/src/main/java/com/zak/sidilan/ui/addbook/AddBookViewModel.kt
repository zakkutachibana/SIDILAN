package com.zak.sidilan.ui.addbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.util.SingleLiveEvent
import org.koin.dsl.module

val addBookViewModelModule = module {
    factory { AddBookViewModel(get()) }
}
class AddBookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _addStatus = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _addStatus

    private val _bookByIsbn = SingleLiveEvent<GoogleBooksResponse>()
    val bookByIsbn: LiveData<GoogleBooksResponse>
        get() = _bookByIsbn

    private val _bookDetail = MutableLiveData<Book?>()
    val bookDetail: MutableLiveData<Book?> get() = _bookDetail
    fun saveBookToFirebase(
        isbn: Long,
        title: String,
        authors: List<String>,
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
            isbn, title, authors, genre, publishedDate, printPrice, sellPrice,
            isPerpetual, startContractDate, endContractDate, createdBy
        ) { isSuccess, message ->
            if (isSuccess) {
                _addStatus.postValue("Added book successfully")
            } else {
                _addStatus.postValue("Failed to add book: $message")
            }
            callback(isSuccess, message)
        }
    }

    fun searchBookByISBN(isbn: String) {
        repository.searchBookByISBN(isbn) { response ->
            if (response.totalItems == 0) {
                _addStatus.postValue("Book not found on Google Books!")
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