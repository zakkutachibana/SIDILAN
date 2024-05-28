package com.zak.sidilan.ui.stockopname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.repositories.BookRepository
import org.koin.dsl.module

val stockOpnameViewModelModule = module {
    factory { StockOpnameViewModel(get()) }
}
class StockOpnameViewModel(val bookRepository: BookRepository) : ViewModel() {
    private val _bookOpnameList = MutableLiveData<List<BookOpname>>()
    val bookOpnameList: LiveData<List<BookOpname>> get() = _bookOpnameList

    fun getBooks() {
        bookRepository.getAllBooks().observeForever { bookList ->
            _bookOpnameList.value = transformAndSortToBookOpname(bookList)
        }
    }

    private fun transformAndSortToBookOpname(bookDetails: ArrayList<BookDetail>): List<BookOpname> {
        val bookOpnameList = ArrayList<BookOpname>()
        for (bookDetail in bookDetails) {
            val bookOpname = BookOpname(
                isbn = bookDetail.book?.isbn,
                bookTitle = bookDetail.book?.title,
                coverUrl = bookDetail.book?.coverUrl.toString(),
                stockActual = 0,
                stockExpected = bookDetail.stock?.stockQty,
                isAppropriate = null,
                discrepancy = 0,
                reason = ""
            )
            bookOpnameList.add(bookOpname)
        }
        return bookOpnameList.sortedBy { it.isAppropriate != null }
    }

    fun updateBookOpname(updatedBook: BookOpname) {
        val currentBooks = _bookOpnameList.value.orEmpty().toMutableList()
        val index = currentBooks.indexOfFirst { it.isbn == updatedBook.isbn }
        if (index != -1) {
            currentBooks[index] = updatedBook
            _bookOpnameList.value = currentBooks.sortedBy { it.isAppropriate != null }
        }
    }

}