package com.zak.sidilan.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val userListViewModelModule = module {
    factory { UserListViewModel(get()) }
}
class UserListViewModel(private val repository: UserRepository): ViewModel() {
    private val _userList = MutableLiveData<ArrayList<User>>()
    val userList: MutableLiveData<ArrayList<User>> get() = _userList

    fun getUsers() {
        repository.getAllUsers().observeForever { userList ->
            _userList.value = userList
        }
    }
}