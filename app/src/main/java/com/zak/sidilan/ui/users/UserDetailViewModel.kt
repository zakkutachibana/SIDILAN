package com.zak.sidilan.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val userDetailViewModelModule = module {
    factory { UserDetailViewModel(get()) }
}
class UserDetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user
    fun getUserById(userId: String){
        repository.getUserById(userId).observeForever { user ->
            _user.value = user
        }
    }
}