package com.zak.sidilan.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.entities.Whitelist
import org.koin.dsl.module

val userRepositoryModule = module {
    factory { UserRepository() }
}
class UserRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersReference = database.reference.child("users")
    private val whitelistReference = database.reference.child("whitelist")

    fun getAllUsers(): MutableLiveData<ArrayList<User>> {
        val userList = MutableLiveData<ArrayList<User>>()

        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = ArrayList<User>()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(User::class.java)
                    user?.let { users.add(it) }
                }
                userList.value = users
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return userList
    }

    fun getAllWhitelist(): MutableLiveData<ArrayList<Whitelist>> {
        val whitelist = MutableLiveData<ArrayList<Whitelist>>()

        whitelistReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = ArrayList<Whitelist>()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(Whitelist::class.java)
                    user?.let { users.add(it) }
                }
                whitelist.value = users
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return whitelist
    }
    fun saveUserToFirebase(
        userId: String,
        displayName: String?,
        email: String?,
        photoUrl: String?
    ): MutableLiveData<String?> {
        val userReference = usersReference.child(userId)
        val status = MutableLiveData<String?>()
        val joinedAt = ServerValue.TIMESTAMP
        val userData = User(userId, displayName, "", email, photoUrl, "", joinedAt)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userReference.child("id").setValue(userId)
                    userReference.child("display_name").setValue(displayName)
                    userReference.child("email").setValue(email)
                    userReference.child("photo_url").setValue(photoUrl)
                        .addOnSuccessListener {
                            status.value = "User data updated successfully"
                        }
                        .addOnFailureListener { e ->
                            status.value = "Error updating user data: $e"
                        }
                } else {
                    userReference.setValue(userData).addOnSuccessListener {
                        status.value = "User data added to database successfully"
                    }
                        .addOnFailureListener { e ->
                            status.value = "Error adding user data to database: $e"
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                status.value = "Database error: $databaseError"
            }
        })


        return status
    }

    fun getUserById(userId: String): MutableLiveData<User?> {
        val userDetailLiveData = MutableLiveData<User?>()

        val userReference = usersReference.child(userId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.getValue(User::class.java)

                userDetailLiveData.value = book
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return userDetailLiveData
    }

    fun addWhitelist(email: String, role: String, phone: String) : MutableLiveData<String?> {
        val id = whitelistReference.push().key ?: ""
        val whitelistData = Whitelist(email, role, phone)

        val status = MutableLiveData<String?>()

        whitelistReference.child(id).setValue(whitelistData)
            .addOnSuccessListener {
                // Data successfully inserted
                status.value = "User added to whitelist: $email"
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                status.value = "Error adding user to whitelist: $e"
            }
        return status
    }
}