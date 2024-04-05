package com.zak.sidilan.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val authViewModelModule = module {
    factory { AuthViewModel(get()) }
}
class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    fun saveUserToFirebase(userId: String, displayName: String, email: String, photoUrl: String) {
        repository.saveUserToFirebase(userId, displayName, email, photoUrl).observeForever {message ->
            _toastMessage.postValue(message)
        }
    }

}