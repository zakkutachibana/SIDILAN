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

    fun getBookCount() {
        _bookCount.value = null
        repository.getBookCount().observeForever { bookCount ->
            _bookCount.value = bookCount
        }
    }

    fun getTotalStockQty() {
        _totalStockQty.value = null
        repository.getTotalStockQuantity().observeForever { bookCount ->
            _totalStockQty.value = bookCount
        }
    }

}