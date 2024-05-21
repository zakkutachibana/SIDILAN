package com.zak.sidilan.ui.trxdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookTrxDetail
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.repositories.TrxRepository
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module
import java.io.File

val trxDetailViewModel = module {
    factory { TrxDetailViewModel(get(), get()) }
}
class TrxDetailViewModel(private val repository: TrxRepository, private val userRepository: UserRepository) : ViewModel() {
    private val _trxDetail = MutableLiveData<BookTrxDetail?>()
    val trxDetail: MutableLiveData<BookTrxDetail?> get() = _trxDetail

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user
    fun getBookTrxById(trxId: String){
        repository.getTrxDetailById(trxId).observeForever { trxDetail ->
            _trxDetail.value = trxDetail
        }
    }

    fun getUserById(userId: String){
        userRepository.getUserById(userId).observeForever { user ->
            _user.value = user
        }
    }

    fun saveInvoicePDF(file : File, callback: (Boolean?) -> Unit) {
        repository.saveInvoicePDF(file) {
            callback(it)
        }
    }
    fun getInvoiceDownloadUrl(invoiceId: String, callback: (String?) -> Unit) {
        repository.getInvoiceDownloadUrl(invoiceId) {
            callback(it)
        }
    }
}