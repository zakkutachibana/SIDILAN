package com.zak.sidilan.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.entities.Whitelist
import com.zak.sidilan.data.repositories.UserRepository
import org.koin.dsl.module

val userManagementViewModelModule = module {
    factory { UserManagementViewModel(get()) }
}
class UserManagementViewModel(private val repository: UserRepository): ViewModel() {

    private val _userList = MutableLiveData<ArrayList<User>>()
    val userList: MutableLiveData<ArrayList<User>> get() = _userList

    private val _whitelist = MutableLiveData<ArrayList<Whitelist>>()
    val whitelist: MutableLiveData<ArrayList<Whitelist>> get() = _whitelist

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun getUsers() {
        repository.getAllUsers().observeForever { userList ->
            _userList.value = userList
        }
    }
    fun getWhitelist() {
        repository.getAllWhitelist().observeForever { whitelist ->
            _whitelist.value = whitelist
        }
    }
    fun addWhitelist(email: String, role: String, phoneNumber: String) {
        repository.addWhitelist(email, role, phoneNumber).observeForever { status ->

        }
    }
}