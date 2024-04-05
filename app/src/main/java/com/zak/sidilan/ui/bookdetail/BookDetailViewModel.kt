package com.zak.sidilan.ui.bookdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.repositories.BookRepository
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val bookDetailViewModelModule = module {
    factory { BookDetailViewModel(get(), get()) }
}
class BookDetailViewModel(private val bookRepository: BookRepository, private val userRepository: UserRepository) : ViewModel() {
    private val _bookDetail = MutableLiveData<BookDetail?>()
    val bookDetail: MutableLiveData<BookDetail?> get() = _bookDetail

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    fun getBookDetailById(bookId: String) {
        _bookDetail.value = null
        bookRepository.getBookDetailById(bookId).observeForever { book ->
            _bookDetail.value = book
        }
    }

    fun deleteBookById(bookId: String, callback: (Boolean, String?) -> Unit) {
        bookRepository.deleteBookById(bookId) { isSuccess, message ->
            callback(isSuccess, message)
        }
    }

    fun getUserById(userId: String){
        userRepository.getUserById(userId).observeForever { user ->
            _user.value = user
        }
    }


    override fun onCleared() {
        super.onCleared()
        _bookDetail.value = null
    }
}