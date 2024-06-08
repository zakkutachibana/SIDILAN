package com.zak.sidilan.ui.stockopname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.Stock
import com.zak.sidilan.data.entities.StockOpname
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.data.repositories.StockOpnameRepository
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val stockOpnameViewModelModule = module {
    factory { StockOpnameViewModel(get(), get(), get()) }
}

class StockOpnameViewModel(
    private val bookRepository: BookRepository,
    private val stockOpnameRepository: StockOpnameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _bookOpnameList = MutableLiveData<List<BookOpname>>()
    val bookOpnameList: LiveData<List<BookOpname>> get() = _bookOpnameList

    private val _stockOpnames = MutableLiveData<ArrayList<StockOpname>>()
    val stockOpname: MutableLiveData<ArrayList<StockOpname>> get() = _stockOpnames

    private val _currentStockOpname = MutableLiveData<StockOpname>()
    val currentStockOpname: MutableLiveData<StockOpname> get() = _currentStockOpname

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    fun getStockOpnames(year: String) {
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
        return bookOpnameList.sortedBy { it.isAppropriate != false }
            .sortedBy { it.isAppropriate != null }
    }

    fun updateBookOpname(updatedBook: BookOpname) {
        val currentBooks = _bookOpnameList.value.orEmpty().toMutableList()
        val index = currentBooks.indexOfFirst { it.isbn == updatedBook.isbn }
        if (index != -1) {
            currentBooks[index] = updatedBook
            _bookOpnameList.value = currentBooks.sortedBy { it.isAppropriate != false }
                .sortedBy { it.isAppropriate != null }
        }
    }

    fun saveStockOpname(
        date: String,
        books: List<BookOpname>,
        createdBy: String,
        period: String,
        overallAppropriate: Boolean,
        status: String,
        callback: (String) -> Unit
    ) {
        stockOpnameRepository.saveStockOpname(
            date,
            books,
            period,
            createdBy,
            overallAppropriate,
            status
        ) {
            callback(it)
        }
    }

    fun getUserById(userId: String) {
        userRepository.getUserById(userId).observeForever { user ->
            _user.value = user
        }
    }

    fun checkCurrentStockOpname(yearMonth: String) {
        stockOpnameRepository.getCurrentStockOpname(yearMonth) { currentStockOpname ->
            if (currentStockOpname != null) {
                syncBooks(currentStockOpname.books.values.filterNotNull())
            } else {
                getBooks()
            }
        }
    }

    fun checkDraftStockOpname(yearMonth: String) {
        stockOpnameRepository.getCurrentStockOpname(yearMonth) { currentStockOpname ->
            _currentStockOpname.value = currentStockOpname
        }
    }

    fun getBooks() {
        bookRepository.getAllBooks().observeForever { bookList ->
            _bookOpnameList.value = transformAndSortToBookOpname(bookList)
        }
    }

    fun updateDiscrepancy(isbn: String, discrpancy: Int) {
        bookRepository.updateDiscrepancy(isbn, discrpancy)
    }

    private fun syncBooks(currentStockOpname: List<BookOpname>) {
        bookRepository.getAllBooks().observeForever { bookList ->
            val currentBooks = transformAndSortToBookOpname(bookList).toMutableList()
            val draftBooks = currentStockOpname.toMutableList()

            val itemsToRemove = draftBooks.filter { draftBook ->
                currentBooks.none { it.isbn == draftBook.isbn }
            }
            draftBooks.removeAll(itemsToRemove)

            val itemsToAdd = currentBooks.filter { currentBook ->
                draftBooks.none { it.isbn == currentBook.isbn }
            }
            draftBooks.addAll(itemsToAdd)

            // Update draftBooks based on currentBooks
            val updatedDraftBooks = draftBooks.map { draftBook ->
                val currentBook = currentBooks.find { it.isbn == draftBook.isbn }
                currentBook?.let { updatedBook ->
                    // If there's a corresponding currentBook, update draftBook
                    if (updatedBook.stockExpected != draftBook.stockExpected) {
                        draftBook.copy(
                            stockActual = updatedBook.stockActual,
                            isAppropriate = updatedBook.isAppropriate,
                            discrepancy = updatedBook.discrepancy,
                            reason = updatedBook.reason,
                            stockExpected = updatedBook.stockExpected
                        )
                    } else {
                        draftBook
                    }
                } ?: draftBook // If no corresponding currentBook found, keep draftBook unchanged
            }
            // Update draftBooks with updated values
            _bookOpnameList.value = updatedDraftBooks.sortedBy { it.isAppropriate != false }.sortedBy { it.isAppropriate != null }
        }
    }
}
