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
import com.zak.sidilan.data.entities.BookDetail
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

    fun getAllBooks(): MutableLiveData<ArrayList<Book>> {
        val bookList = MutableLiveData<ArrayList<Book>>()

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
        return bookList
    }
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
    fun updateBookFirebase(
        bookId: String,
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
        callback: (Boolean, String?) -> Unit
    ) {
        val book = Book(
            bookId, isbn, title, authors, genre, publishedDate, printPrice,
            sellPrice, isPerpetual, startContractDate, endContractDate
        )
        val bookMap = mutableMapOf<String, Any>()
        bookMap["book"] = book

        reference.child(bookId).updateChildren(bookMap).addOnCompleteListener { task ->
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

    fun getBookDetailById(bookId: String): LiveData<BookDetail?> {
        val bookDetailLiveData = MutableLiveData<BookDetail?>()

        val bookReference = reference.child(bookId)
        bookReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.child("book").getValue(Book::class.java)
                val logs = snapshot.child("logs").getValue(Logs::class.java)

                val bookDetail = BookDetail(book, logs)
                bookDetailLiveData.value = bookDetail
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return bookDetailLiveData
    }

    fun deleteBookById(bookId: String, callback: (Boolean, String?) -> Unit) {
        reference.child(bookId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, task.exception?.message)
            }
        }
    }

    fun getBookCount(): MutableLiveData<Long?> {
        val bookCount = MutableLiveData<Long?>()
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bookCount.value = dataSnapshot.childrenCount
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        return bookCount
    }

    fun getTotalStockQuantity(): MutableLiveData<Long?> {
        val totalStockQty = MutableLiveData<Long?>()

        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sumStockQty: Long = 0

                for (childSnapshot in dataSnapshot.children) {
                    val stockQty = childSnapshot.child("book").child("stock_qty").getValue(Long::class.java)
                    stockQty?.let { sumStockQty += it }
                }
                totalStockQty.postValue(sumStockQty)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        return totalStockQty
    }
}