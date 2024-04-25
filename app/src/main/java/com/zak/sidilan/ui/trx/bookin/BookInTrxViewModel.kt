package com.zak.sidilan.ui.trx.bookin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookPrice
import org.koin.dsl.module

val bookInTrxViewModelModule = module {
    factory { BookInTrxViewModel() }
}
class BookInTrxViewModel : ViewModel() {
    private val _selectedBooksList = MutableLiveData<List<BookPrice>>()
    val selectedBooksList: LiveData<List<BookPrice>> = _selectedBooksList

    fun addBook(book: BookPrice) {
        val currentBooks = _selectedBooksList.value.orEmpty().toMutableList()
        currentBooks.add(book)
        _selectedBooksList.value = currentBooks
    }

    fun removeBook(book: BookPrice) {
        val currentBooks = _selectedBooksList.value.orEmpty().toMutableList()
        currentBooks.remove(book)
        _selectedBooksList.value = currentBooks
    }
}