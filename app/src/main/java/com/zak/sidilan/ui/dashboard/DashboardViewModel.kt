package com.zak.sidilan.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.repositories.BookRepository
import org.koin.dsl.module

val dashboardViewModelModule = module {
    factory { DashboardViewModel(get()) }
}
class DashboardViewModel(private val repository: BookRepository) : ViewModel() {

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

    fun getBookCount() {
        _bookCount.value = null
        repository.getBookCount().observeForever { bookCount ->
            _bookCount.value = bookCount
        }
    }

    fun getTotalStockQty() {
        _totalStockQty.value = null
        repository.getTotalStockQuantity().observeForever { totalStockQty ->
            _totalStockQty.value = totalStockQty
        }
    }

    fun getTotalValue() {
        _totalValue.value = null
        repository.getTotalValue().observeForever { totalValue ->
            _totalValue.value = totalValue
        }
    }

    fun getTotalSalesQty() {
        _totalSalesQty.value = null
        repository.getTotalSalesQuantity().observeForever { totalSalesQty ->
            _totalSalesQty.value = totalSalesQty
        }
    }

    fun getTotalSales() {
        _totalSales.value = null
        repository.getTotalSales().observeForever { totalSales ->
            _totalSales.value = totalSales
        }
    }

}