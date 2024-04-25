package com.zak.sidilan.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.entities.Whitelist
import com.zak.sidilan.util.UserRole
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

    fun isEmailWhitelisted(email: String, callback: (Boolean, Whitelist?) -> Unit) {
        val whitelistReference = database.reference.child("whitelist")
        whitelistReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val entry = dataSnapshot.children.first().getValue(Whitelist::class.java)
                        callback(true, entry)

                    } else {
                        callback(false, null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //something
                }

            })
    }

    fun getUserRole(userId: String, callback: (UserRole?, Exception?) -> Unit) {
        val userRef = usersReference.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val role = dataSnapshot.child("role").getValue(String::class.java)
                    val userRole = parseUserRole(role)
                    callback(userRole, null)
                } else {
                    // User not found
                    callback(null, IllegalArgumentException("User not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error occurred
                callback(null, databaseError.toException())
            }
        })
    }

    private fun parseUserRole(role: String?): UserRole? {
        return when (role) {
            "Admin" -> UserRole.ADMIN
            "Direktur" -> UserRole.DIRECTOR
            "Manajer" -> UserRole.MANAGER
            "Staf" -> UserRole.STAFF
            else -> null
        }
    }

    fun saveUserToFirebase(
        userId: String,
        displayName: String?,
        email: String?,
        photoUrl: String?,
        role: String?,
        phoneNumber: String?,
    ): MutableLiveData<String?> {
        val userReference = usersReference.child(userId)
        val joinedAt = ServerValue.TIMESTAMP
        val userData = User(userId, displayName, role, email, photoUrl, phoneNumber, joinedAt)
        val status = MutableLiveData<String?>()


        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userReference.child("id").setValue(userId)
                    userReference.child("display_name").setValue(displayName)
                    userReference.child("email").setValue(email)
                    userReference.child("photo_url").setValue(photoUrl)
                    userReference.child("role").setValue(role)
                    userReference.child("phone_number").setValue(phoneNumber)
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

    fun getWhitelistById(whitelistId: String): MutableLiveData<Whitelist?> {
        val whitelistDetailLiveData = MutableLiveData<Whitelist?>()

        val whitelistReference = whitelistReference.child(whitelistId)
        whitelistReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val whitelist = snapshot.getValue(Whitelist::class.java)

                whitelistDetailLiveData.value = whitelist
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return whitelistDetailLiveData
    }

    fun addWhitelist(email: String, role: String, phone: String): MutableLiveData<String?> {
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