package com.zak.sidilan.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.Whitelist
import com.zak.sidilan.data.repositories.UserRepository
import com.zak.sidilan.util.UserRole
import org.koin.dsl.module

val authViewModelModule = module {
    factory { AuthViewModel(get()) }
}
class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    fun saveUserToFirebase(userId: String, displayName: String, email: String, photoUrl: String, role: String, phoneNumber: String) {
        repository.saveUserToFirebase(userId, displayName, email, photoUrl, role, phoneNumber).observeForever {message ->
            _toastMessage.postValue(message)
        }
    }

    fun isEmailWhitelisted(email: String, callback: (Boolean, Whitelist?) -> Unit) {
        repository.isEmailWhitelisted(email) { whitelisted, additionalInfo ->
            callback(whitelisted, additionalInfo)
        }
    }

    fun checkRole(userId: String, callback: (UserRole?, Exception?) -> Unit) {
        repository.getUserRole(userId) { userRole, exception ->
            callback(userRole, exception)
        }
    }

}