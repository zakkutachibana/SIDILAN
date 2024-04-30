package com.zak.sidilan.ui.trx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookQtyPrice
import com.zak.sidilan.data.repositories.TrxRepository
import org.koin.dsl.module

val bookTrxViewModelModule = module {
    factory { BookTrxViewModel(get()) }
}
class BookTrxViewModel(private val repository: TrxRepository) : ViewModel() {
    private val _selectedBooksList = MutableLiveData<List<BookQtyPrice>>()
    val selectedBooksList: LiveData<List<BookQtyPrice>> = _selectedBooksList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun addBook(book: BookQtyPrice) {
        val currentBooks = _selectedBooksList.value.orEmpty().toMutableList()
        val existingBookIndex = currentBooks.indexOfFirst { it.book == book.book }

        if (existingBookIndex != -1) {
            val existingBook = currentBooks[existingBookIndex]
            existingBook.bookQty += book.bookQty
            existingBook.bookPrice += book.bookPrice
            _selectedBooksList.value = currentBooks
        } else {
            currentBooks.add(book)
            _selectedBooksList.value = currentBooks
        }
    }

    fun removeBook(book: BookQtyPrice) {
        val currentBooks = _selectedBooksList.value.orEmpty().toMutableList()
        currentBooks.remove(book)
        _selectedBooksList.value = currentBooks
    }

    fun updateQty(updatedBook: BookQtyPrice) {
        val currentBooks = _selectedBooksList.value.orEmpty().toMutableList()
        val index = currentBooks.indexOfFirst { it.book == updatedBook.book }
        if (index != -1) {
            currentBooks[index] = updatedBook
            _selectedBooksList.value = currentBooks
        }
    }

    fun addTrxPrint(trx: BookInPrintingTrx) {
        repository.addBookInPrintTrx(trx).observeForever { status ->
            _toastMessage.value = status
        }
    }

    fun clearSelectedBooksList() {
        _selectedBooksList.value = emptyList()
    }
}