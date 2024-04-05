package com.zak.sidilan.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zak.sidilan.data.entities.User

object AuthManager {
    private var currentUser: FirebaseUser? = null

    fun init(context: Context) {
        val auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
    }
    fun setCurrentUser(user: FirebaseUser?) {
        currentUser = user
    }

    fun getCurrentUser(): User {
        return User(
            currentUser?.uid.toString(),
            currentUser?.displayName,
            currentUser?.email,
            currentUser?.photoUrl.toString().replace("s96-c", "s300-c")
        )
    }
}