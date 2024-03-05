package com.zak.sidilan.ui.books

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.Book

class BooksViewModel : ViewModel() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("books")
    val bookList = MutableLiveData<ArrayList<Book>>()

    fun getBooks() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = ArrayList<Book>()
                for (eachBook in snapshot.children) {
                    val book = eachBook.child("book").getValue(Book::class.java)
                    book?.let { books.add(it) }
                }
                bookList.value = books
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
