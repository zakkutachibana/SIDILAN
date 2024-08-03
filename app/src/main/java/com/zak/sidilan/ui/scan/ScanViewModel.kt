package com.zak.sidilan.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.ui.addbook.AddBookViewModel
import com.zak.sidilan.util.SingleLiveEvent
import org.koin.dsl.module

val scanViewModelModule = module {
    factory { ScanViewModel(get()) }
}
class ScanViewModel(private val repository: BookRepository) : ViewModel() {

    private val _addStatus = SingleLiveEvent<String>()
    val toastMessage: LiveData<String>
        get() = _addStatus

    private val _bookByIsbn = SingleLiveEvent<GoogleBooksResponse>()
    val bookByIsbn: LiveData<GoogleBooksResponse>
        get() = _bookByIsbn

    fun searchBookByISBN(isbn: String) {
        repository.searchBookByISBN(isbn) { response ->
            if (response.totalItems == 0) {
                _addStatus.postValue("Book not found on Google Books!")

            } else {
                _bookByIsbn.postValue(response)
            }
        }
    }
}