package com.zak.sidilan.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.entities.Whitelist
import com.zak.sidilan.data.repositories.UserRepository
import com.zak.sidilan.util.UserRole
import org.koin.dsl.module

val authViewModelModule = module {
    factory { AuthViewModel(get()) }
}
class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun saveUserToFirebase(userId: String, displayName: String, email: String, photoUrl: String, role: String, phoneNumber: String) {
        repository.saveUserToFirebase(userId, displayName, email, photoUrl, role, phoneNumber).observeForever { message ->
            _toastMessage.postValue(message)
        }
    }

    fun isEmailWhitelisted(email: String, callback: (Boolean, Whitelist?) -> Unit) {
        repository.isEmailWhitelisted(email) { whitelisted, additionalInfo ->
            callback(whitelisted, additionalInfo)
        }
    }

    fun getCurrentUser(userId: String) {
        _currentUser.value = null
        repository.getCurrentUser(userId).observeForever { user ->
            _currentUser.value = user
        }
    }

    fun getCurrentUserOnce(userId: String) {
        _currentUser.value = null
        repository.getCurrentUserOnce(userId).observeForever { user ->
            _currentUser.value = user
        }
    }

    fun validateWhitelist(email: String, callback: (Boolean) -> Unit) {
        repository.validateWhitelistOnce(email) { whitelisted ->
            callback(whitelisted)
        }
    }

}