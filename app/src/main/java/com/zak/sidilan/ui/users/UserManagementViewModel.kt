package com.zak.sidilan.ui.users

import android.util.Log
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

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    private val whitelistedEmails = mutableSetOf<String>()

    fun getUsers() {
        repository.getAllUsers().observeForever { userList ->
            userList.forEach { user ->
                validateWhitelist(user.email.toString()) { isWhitelisted ->
                    if (isWhitelisted) {
                        whitelistedEmails.add(user.email.toString())
                    } else {
                        whitelistedEmails.remove(user.email)
                    }

                    val sortedUsers = userList.sortedByDescending { user ->
                        whitelistedEmails.contains(user.email)
                    }

                    _userList.postValue(ArrayList(sortedUsers))
                }
            }
        }
    }

    fun getUserById(userId: String){
        repository.getUserById(userId).observeForever { user ->
            _user.value = user
        }
    }
    fun getUserByEmail(userId: String, callback: (User?) -> Unit){
        repository.getUserByEmail(userId) { user ->
            callback(user)
        }
    }

    fun filterUsersByRoles(selectedRoles: List<String>) {
        repository.getAllUsers().observeForever { allUsers ->
            val filteredUsers = if (selectedRoles.isNotEmpty()) {
                allUsers.filter { user -> selectedRoles.contains(user.role) }
            } else {
                allUsers
            }
            _userList.postValue(ArrayList(filteredUsers))
        }
    }

    fun getWhitelist() {
        repository.getAllWhitelist().observeForever { whitelist ->
            _whitelist.value = whitelist
        }
    }

    fun addWhitelist(email: String, role: String, phoneNumber: String) {
        repository.addWhitelist(email, role, phoneNumber)
    }

    fun updateWhitelist(email: String, role: String, phoneNumber: String, callback: (String) -> Unit) {
        repository.updateWhitelist(email, role, phoneNumber) { status ->
            callback(status)
        }
    }

    fun updateRole(email: String, newRole: String, callback: (String) -> Unit) {
        repository.updateRole(email, newRole) { status ->
            callback(status)
            Log.d("UpdateRole", status)
        }
    }

    fun deleteWhitelist(email: String, callback: (String) -> Unit) {
        repository.deleteWhitelist(email) { status ->
            callback(status)
        }
    }

    fun validateWhitelist(email: String, callback: (Boolean) -> Unit) {
        repository.validateWhitelist(email) {
            callback(it)
        }
    }

    fun validateWhitelistOnce(email: String, callback: (Boolean) -> Unit) {
        repository.validateWhitelistOnce(email) {
            callback(it)
        }
    }

    fun validateWhitelistRegistered(email: String, callback: (Boolean) -> Unit) {
        repository.validateWhitelistRegistered(email) {
            callback(it)
        }
    }
}