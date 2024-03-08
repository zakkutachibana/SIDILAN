package com.zak.sidilan.ui.bookdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.entities.Book

class BookDetailViewModel : ViewModel() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("books")

    private val _bookDetail = MutableLiveData<Book?>()
    val bookDetail: MutableLiveData<Book?> get() = _bookDetail

    fun getBookDetailById(bookId: String) {
        val bookReference = reference.child(bookId)
        bookReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.child("book").getValue(Book::class.java)
                _bookDetail.value = book
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}