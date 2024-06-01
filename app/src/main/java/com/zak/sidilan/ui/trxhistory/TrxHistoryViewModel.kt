package com.zak.sidilan.ui.trxhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.entities.BookTrxDetail
import com.zak.sidilan.data.repositories.TrxRepository
import org.koin.dsl.module

val trxHistoryViewModelModule = module {
    factory { TrxHistoryViewModel(get()) }
}
class TrxHistoryViewModel(private val repository: TrxRepository) : ViewModel() {

    private val _trxList = MutableLiveData<ArrayList<BookTrxDetail>>()
    val trxList: MutableLiveData<ArrayList<BookTrxDetail>> get() = _trxList

    private val _trxSellingList = MutableLiveData<List<BookTrxDetail>>()
    val trxSellingList: MutableLiveData<List<BookTrxDetail>> get() = _trxSellingList

    fun getTrx() {
        repository.getAllTrx().observeForever { trxList ->
            _trxList.value = trxList
        }
    }

    fun calculateTotalBookQty() {
        repository.getAllTrx().observeForever { transactions ->
            val trxSellList = transactions.filter { it.bookTrx is BookOutSellingTrx }
            _trxSellingList.value = trxSellList
        }
    }
}