package com.zak.sidilan.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.BuildConfig
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.retrofit.ApiConfig
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val bookRepositoryModule = module {
    factory { BookRepository() }
}
class BookRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("books")

    fun saveBookToFirebase(
        isbn: Long,
        title: String,
        authors: List<String>,
        genre: String,
        publishedDate: String,
        printPrice: Long,
        sellPrice: Long,
        isPerpetual: Boolean,
        startContractDate: String?,
        endContractDate: String?,
        createdBy: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val id = reference.push().key ?: return
        val book = Book(
            id, isbn, title, authors, genre, publishedDate, printPrice,
            sellPrice, isPerpetual, startContractDate, endContractDate
        )
        val createdAt = ServerValue.TIMESTAMP
        val logs = Logs(createdBy, createdAt)

        val bookMap = mutableMapOf<String, Any>()
        bookMap["book"] = book
        bookMap["logs"] = logs

        reference.child(id).setValue(bookMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, task.exception?.message)
            }
        }
    }

    fun searchBookByISBN(isbn: String, callback: (GoogleBooksResponse) -> Unit) {
        val call = ApiConfig.instance.getBookByIsbn("isbn:$isbn", BuildConfig.GOOGLE_BOOKS_API_KEY)
        call.enqueue(object : Callback<GoogleBooksResponse> {
            override fun onResponse(
                call: Call<GoogleBooksResponse>,
                response: Response<GoogleBooksResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(it)
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

    fun getBookDetailById(bookId: String): LiveData<Book?> {
        val bookDetailLiveData = MutableLiveData<Book?>()

        val bookReference = reference.child(bookId)
        bookReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.child("book").getValue(Book::class.java)
                bookDetailLiveData.value = book
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return bookDetailLiveData
    }
}