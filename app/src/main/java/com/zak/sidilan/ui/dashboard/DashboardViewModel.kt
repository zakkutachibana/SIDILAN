package com.zak.sidilan.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.data.repositories.TrxRepository
import com.zak.sidilan.util.Formatter.toDate
import org.koin.dsl.module

val dashboardViewModelModule = module {
    factory { DashboardViewModel(get(), get()) }
}

class DashboardViewModel(
    private val bookRepository: BookRepository,
    private val trxRepository: TrxRepository
) : ViewModel() {

    private val _bookCount = MutableLiveData<Long?>()
    val bookCount: LiveData<Long?> get() = _bookCount

    private val _totalStockQty = MutableLiveData<Long?>()
    val totalStockQty: LiveData<Long?> get() = _totalStockQty

    private val _totalValue = MutableLiveData<Long?>()
    val totalValue: LiveData<Long?> get() = _totalValue

    private val _totalSalesQty = MutableLiveData<Long?>()
    val totalSalesQty: LiveData<Long?> get() = _totalSalesQty

    private val _totalSales = MutableLiveData<Long?>()
    val totalSales: LiveData<Long?> get() = _totalSales

    private val _totalSalesUndone = MutableLiveData<Int?>()
    val totalSalesUndone: LiveData<Int?> get() = _totalSalesUndone

    private val _totalBookContractDone = MutableLiveData<Int?>()
    val totalBookContractDone: LiveData<Int?> get() = _totalBookContractDone

    fun getBookCount() {
        _bookCount.value = null
        bookRepository.getBookCount().observeForever { bookCount ->
            _bookCount.value = bookCount
        }
    }

    fun getTotalStockQty() {
        _totalStockQty.value = null
        bookRepository.getTotalStockQuantity().observeForever { totalStockQty ->
            _totalStockQty.value = totalStockQty
        }
    }

    fun getTotalValue() {
        _totalValue.value = null
        bookRepository.getTotalValue().observeForever { totalValue ->
            _totalValue.value = totalValue
        }
    }

    fun getSales() {
        trxRepository.getAllTrx().observeForever { transactions ->
            val trxSellList = transactions.filter { it.bookTrx is BookOutSellingTrx }
            val totalBookQty = trxSellList.sumOf { (it.bookTrx as BookOutSellingTrx).totalBookQty }
            val totalBookPrice = trxSellList.sumOf { (it.bookTrx as BookOutSellingTrx).finalPrice }

            _totalSalesQty.value = totalBookQty
            _totalSales.value = totalBookPrice
        }
    }

    fun getSalesUndone() {
        trxRepository.getAllTrx().observeForever { transactions ->
            val trxSellList = transactions.filter { it.bookTrx is BookOutSellingTrx }
            val totalUndone = trxSellList.count { (it.bookTrx as BookOutSellingTrx).isPaid == false }

            _totalSalesUndone.value = totalUndone

        }
    }

    fun getFilteredSales(startDateStr: String, endDateStr: String) {
        val startDate = startDateStr.toDate()
        val endDate = endDateStr.toDate()

        if (startDate != null && endDate != null) {
            trxRepository.getAllTrx().observeForever { transactions ->
                val filteredTrx = transactions.filter { transaction ->
                    if (transaction.bookTrx is BookOutSellingTrx) {
                        val bookOutDate = transaction.bookTrx.bookOutDate.toDate()
                        bookOutDate != null && !bookOutDate.before(startDate) && !bookOutDate.after(endDate)
                    } else {
                        false
                    }
                }

                // Sum the total quantities and prices
                val totalBookQty = filteredTrx.sumOf { (it.bookTrx as BookOutSellingTrx).totalBookQty }
                val totalBookPrice = filteredTrx.sumOf { (it.bookTrx as BookOutSellingTrx).finalPrice }

                _totalSalesQty.value = totalBookQty
                _totalSales.value = totalBookPrice

                println("Total Book Quantity Sold: $totalBookQty")
                println("Total Book Price Sold: $totalBookPrice")
            }
        } else {
            // Handle the case where the date conversion failed
            println("Invalid date format")
        }
    }

    fun getFilteredBookContract(endDateStr: String) {
        val endDate = endDateStr.toDate()

        if (endDate != null) {
            bookRepository.getAllBooks().observeForever { books ->
                val filteredBooks = books.filter { book ->
                    if (book.book?.isPerpetual == false) {
                        val bookContractEnd = book.book.endContractDate?.toDate()
                        bookContractEnd != null && bookContractEnd.before(endDate)
                    } else {
                        false
                    }
                }

                // Sum the total quantities and prices
                val totalContractDone = filteredBooks.count()
                _totalBookContractDone.value = totalContractDone
            }
        } else {
            // Handle the case where the date conversion failed
            println("Invalid date format")
        }
    }

}