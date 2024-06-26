package com.zak.sidilan.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.repositories.BookRepository
import org.koin.dsl.module


val booksViewModelModule = module {
    factory { BooksViewModel(get()) }
}

class BooksViewModel(private val repository: BookRepository) : ViewModel() {

    private val _bookList = MutableLiveData<ArrayList<BookDetail>>()
    val bookList: MutableLiveData<ArrayList<BookDetail>> get() = _bookList

    private val _sortedBySold = MutableLiveData<ArrayList<BookDetail>>()
    val sortedBySold: MutableLiveData<ArrayList<BookDetail>> get() = _sortedBySold

    val isBookListEmpty: LiveData<Boolean> = bookList.map { books ->
        books.isEmpty()
    }

    fun getBooks() {
        repository.getAllBooks().observeForever { bookList ->
            val sortedBooks = bookList.sortedByDescending { it.stock?.soldQty ?: 0 }
            _sortedBySold.value = ArrayList(sortedBooks.take(10))
            _bookList.value = bookList
        }
    }
    
    fun filterBooks(query: String) {
        repository.getAllBooks().observeForever { allBooks ->
            val filteredBooks = if (query.isNotEmpty()) {
                allBooks.filter {
                    it.book?.title?.contains(query, ignoreCase = true) == true ||
                            it.book?.authors?.any { author -> author.contains(query, ignoreCase = true) } == true
                }
            } else {
                allBooks
            }
            _bookList.postValue(ArrayList(filteredBooks))
        }
    }


}

