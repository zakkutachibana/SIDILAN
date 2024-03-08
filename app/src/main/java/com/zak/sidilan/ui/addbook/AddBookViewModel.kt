package com.zak.sidilan.ui.addbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.zak.sidilan.BuildConfig
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBookViewModel : ViewModel() {
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var reference: DatabaseReference = database.reference.child("books")
    private val _addStatus = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _addStatus
    fun saveBookToFirebase(
        isbn: Long,
        title: String,
        authors: List<String>,
        genre: String,
        publishedDate: String,
        printPrice: Double,
        sellPrice: Double,
        isPerpetual: Boolean,
        startContractDate: String?,
        endContractDate: String?,
        createdBy: String,
        callback: (Boolean) -> Unit
    ) {
        val id = reference.push().key ?: return
        val createdAt = ServerValue.TIMESTAMP

        val book = Book(
            id, isbn, title, authors, genre, publishedDate,
            printPrice, sellPrice, isPerpetual, startContractDate, endContractDate
        )
        val logs = Logs(createdBy, createdAt)

        val bookMap = mutableMapOf<String, Any>()
        bookMap["book"] = book
        bookMap["logs"] = logs

        reference.child(id).setValue(bookMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _addStatus.value = "Added book successfully"
                callback(true)
            } else {
                _addStatus.value = "Failed to add book: ${task.exception?.message}"
                callback(false)
            }
        }
    }

    fun searchBookByISBN(isbn: String) {
        val call = ApiConfig.instance.getBookByIsbn(isbn, BuildConfig.GOOGLE_BOOKS_API_KEY)
        call.enqueue(object : Callback<GoogleBooksResponse> {
            override fun onResponse(
                call: Call<GoogleBooksResponse>,
                response: Response<GoogleBooksResponse>
            ) {
                if (response.isSuccessful) {
                    when (response.body()?.totalItems) {
                        0 -> _addStatus.value = "Buku tidak ditemukan di Google Books!"
                        else -> {
                            val book = response.body()
                        }

                    }
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<GoogleBooksResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }
}