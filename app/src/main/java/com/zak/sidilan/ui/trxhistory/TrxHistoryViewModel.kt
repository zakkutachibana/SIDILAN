package com.zak.sidilan.ui.trxhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookTrx
import com.zak.sidilan.data.repositories.TrxRepository
import org.koin.dsl.module

val trxHistoryViewModelModule = module {
    factory { TrxHistoryViewModel(get()) }
}
class TrxHistoryViewModel(private val repository: TrxRepository) : ViewModel() {

    private val _trxList = MutableLiveData<ArrayList<BookTrx>>()
    val trxList: MutableLiveData<ArrayList<BookTrx>> get() = _trxList
    fun getTrx() {
        repository.getAllTrx().observeForever { trxList ->
            _trxList.value = trxList
        }
    }
}