package com.zak.sidilan.ui.stockopname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.StockOpname
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.data.repositories.StockOpnameRepository
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val stockOpnameViewModelModule = module {
    factory { StockOpnameViewModel(get(), get(), get()) }
}
class StockOpnameViewModel(val bookRepository: BookRepository, val stockOpnameRepository: StockOpnameRepository, val userRepository: UserRepository) : ViewModel() {
    private val _bookOpnameList = MutableLiveData<List<BookOpname>>()
    val bookOpnameList: LiveData<List<BookOpname>> get() = _bookOpnameList
    private val _stockOpnames = MutableLiveData<ArrayList<StockOpname>>()
    val stockOpname: MutableLiveData<ArrayList<StockOpname>> get() = _stockOpnames
    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    fun getBooks() {
        bookRepository.getAllBooks().observeForever { bookList ->
            _bookOpnameList.value = transformAndSortToBookOpname(bookList)
        }
    }

    fun getStockOpname(year: String) {
        stockOpnameRepository.getAllStockOpname(year).observeForever { stockOpnameArrayList ->
            _stockOpnames.value = stockOpnameArrayList
        }
    }
    private fun transformAndSortToBookOpname(bookDetails: ArrayList<BookDetail>): List<BookOpname> {
        val bookOpnameList = ArrayList<BookOpname>()
        for (bookDetail in bookDetails) {
            val bookOpname = BookOpname(
                isbn = bookDetail.book?.isbn,
                bookTitle = bookDetail.book?.title,
                coverUrl = bookDetail.book?.coverUrl.toString(),
                stockActual = bookDetail.stock?.stockQty,
                stockExpected = bookDetail.stock?.stockQty,
                isAppropriate = null,
                discrepancy = 0,
                reason = ""
            )
            bookOpnameList.add(bookOpname)
        }
        return bookOpnameList.sortedBy{ it.isAppropriate != false }.sortedBy{ it.isAppropriate != null }
    }

    fun updateBookOpname(updatedBook: BookOpname) {
        val currentBooks = _bookOpnameList.value.orEmpty().toMutableList()
        val index = currentBooks.indexOfFirst { it.isbn == updatedBook.isbn }
        if (index != -1) {
            currentBooks[index] = updatedBook
            _bookOpnameList.value = currentBooks.sortedBy{ it.isAppropriate != false }.sortedBy{ it.isAppropriate != null }
        }
    }
    fun saveStockOpname(
        date: String,
        books: List<BookOpname>,
        createdBy: String,
        period: String,
        overallAppropriate: Boolean,
        callback: (String) -> Unit
    ) {
        stockOpnameRepository.saveStockOpname(date, books, period, createdBy, overallAppropriate) {
            callback(it)
        }
    }

    fun getUserById(userId: String){
        userRepository.getUserById(userId).observeForever { user ->
            _user.value = user
        }
    }

}