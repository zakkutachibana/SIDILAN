package com.zak.sidilan.ui.trxhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.entities.BookTrxDetail
import com.zak.sidilan.data.repositories.TrxRepository
import com.zak.sidilan.util.Formatter.toDate
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

    fun getAllSalesTrx() {
        repository.getAllTrx().observeForever { transactions ->
            val trxSellList = transactions.filter { it.bookTrx is BookOutSellingTrx }
            _trxSellingList.value = trxSellList
        }
    }

    fun filterTrxByType(selectedTrx: List<String>) {
        repository.getAllTrx().observeForever { trxList ->
            val filteredTrx = if (selectedTrx.isNotEmpty()) {
                trxList.filter { transaction -> selectedTrx.any { transaction.bookTrx?.id?.startsWith(it) ?: false } }
            } else {
                trxList
            }
            _trxList.postValue(ArrayList(filteredTrx))
        }
    }

    fun getFilteredSalesByDate(startDateStr: String, endDateStr: String) {
        val startDate = startDateStr.toDate()
        val endDate = endDateStr.toDate()

        if (startDate != null && endDate != null) {
            repository.getAllTrx().observeForever { transactions ->
                val filteredTrx = transactions.filter { transaction ->
                    if (transaction.bookTrx is BookOutSellingTrx) {
                        val bookOutDate = transaction.bookTrx.bookOutDate.toDate()
                        bookOutDate != null && !bookOutDate.before(startDate) && !bookOutDate.after(endDate)
                    } else {
                        false
                    }
                }
                _trxSellingList.postValue(ArrayList(filteredTrx))

            }
        } else {
            // Handle the case where the date conversion failed
            println("Invalid date format")
        }
    }
}