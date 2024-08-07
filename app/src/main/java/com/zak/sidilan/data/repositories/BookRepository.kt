package com.zak.sidilan.data.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.zak.sidilan.BuildConfig
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.Stock
import com.zak.sidilan.data.entities.GoogleBooksResponse
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.retrofit.ApiConfig
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

val bookRepositoryModule = module {
    factory { BookRepository() }
}
class BookRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val reference = database.reference.child("books")

    fun getAllBooks(): MutableLiveData<ArrayList<BookDetail>> {
        val bookList = MutableLiveData<ArrayList<BookDetail>>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = ArrayList<BookDetail>()
                for (eachBook in snapshot.children) {
                    val book = eachBook.getValue(BookDetail::class.java)
                    book?.let { books.add(it) }
                }
                bookList.value = books
                Log.d("SUCCESS GET ALL BOOK", "${bookList.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return bookList
    }

    fun saveBookToFirebase(book: Book, createdBy: String, callback: (Boolean, String?) -> Unit) {
        // Upload the image to Firebase Storage
        if (book.coverUrl != null) {
            // Upload the image to Firebase Storage
            val imageRef = storage.reference.child("book_covers/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(book.coverUrl as Uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                // Image uploaded successfully, get the download URL
                val coverUrl = uri.toString()
                book.coverUrl = coverUrl
                // Save the book details to the Realtime Database
                saveBookDetailsToDatabase(book, createdBy, callback)
            }.addOnFailureListener { exception ->
                // Handle any errors during image upload
                callback(false, exception.message)
            }
        } else {
            val storageReference = FirebaseStorage.getInstance().getReference("book_covers/placeholder.jpg") // Replace "placeholder.jpg" with the actual filename
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val placeholderUrl = uri.toString()
                book.coverUrl = placeholderUrl
                saveBookDetailsToDatabase(book, createdBy, callback)
            }.addOnFailureListener { exception ->
                callback(false, exception.message)
            }
        }
    }

    private fun saveBookDetailsToDatabase(book: Book, createdBy: String, callback: (Boolean, String?) -> Unit) {
        val initialStock = Stock(0)
        val createdAt = ServerValue.TIMESTAMP
        val logs = Logs(createdBy, createdAt)

        val bookMap = mutableMapOf<String, Any>()
        bookMap["book"] = book
        bookMap["logs"] = logs
        bookMap["stock"] = initialStock

        reference.child(book.isbn.toString()).setValue(bookMap).addOnCompleteListener { dbTask ->
            if (dbTask.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, dbTask.exception?.message)
            }
        }
    }

    fun updateBookFirebase(book: Book, oldCover: String, callback: (Boolean, String?) -> Unit) {
        if (book.coverUrl != null) {
            val imageRef = storage.reference.child("book_covers/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(Uri.parse(book.coverUrl.toString()))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                val newCoverUrl = uri.toString()
                if (book.coverUrl != null) {
                    val previousImageRef = storage.getReferenceFromUrl(oldCover)
                    previousImageRef.delete()
                }
                book.coverUrl = newCoverUrl
                val bookMap = mutableMapOf<String, Any>()
                bookMap["book"] = book
                reference.child(book.isbn.toString()).updateChildren(bookMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, "${task.exception?.message}")
                    }
                }
            }.addOnFailureListener { exception ->
                callback(false, "${exception.message}")
            }
        } else {
            // Handle the scenario when the book doesn't have a cover URL
            callback(false, "Book cover URL is null.")
        }
    }

    fun validateBookId(isbn: String, callback: (Boolean) -> Unit) {
        reference.child(isbn).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bookExists = dataSnapshot.exists()
                callback(bookExists)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback(false)
            }
        })
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

    fun getBookDetailById(isbn: String): LiveData<BookDetail?> {
        val bookDetailLiveData = MutableLiveData<BookDetail?>()

        val bookReference = reference.child(isbn)
        bookReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.child("book").getValue(Book::class.java)
                val logs = snapshot.child("logs").getValue(Logs::class.java)
                val stock = snapshot.child("stock").getValue(Stock::class.java)

                val bookDetail = BookDetail(book, logs, stock)
                bookDetailLiveData.value = bookDetail
                Log.d("DATA BUKU", bookDetail.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return bookDetailLiveData
    }
    fun getBookCurrentStock(isbn: String, callback : (Long?) -> Unit){
        val bookReference = reference.child(isbn)
        bookReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stock = snapshot.child("stock").getValue(Stock::class.java)
                callback(stock?.stockQty)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
    fun deleteBookById(isbn: String, callback: (Boolean, String?) -> Unit) {
        reference.child(isbn).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coverUrl = snapshot.child("book").child("cover_url").getValue(String::class.java)
                val previousImageRef = coverUrl?.let { storage.getReferenceFromUrl(it) }
                previousImageRef?.delete()

                reference.child(isbn).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, task.exception?.message)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // STOCK STUFF
    fun updateBookStock(isbn: String, transactionType: String, quantity: Long) {
        val stockRef = reference.child(isbn).child("stock")

        stockRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val stock = dataSnapshot.getValue(Stock::class.java) ?: Stock()

                val updatedStockQty = when (transactionType) {
                    "book_in_printing", "book_in_donation", "discrepancy_plus" -> stock.stockQty + quantity
                    "book_out_selling", "book_out_donation", "discrepancy_minus" -> stock.stockQty - quantity
                    else -> stock.stockQty
                }

                val updatedSoldQty = when (transactionType) {
                    "book_out_selling" -> stock.soldQty + quantity
                    else -> stock.soldQty
                }

                val updatedPrintedQty = when (transactionType) {
                    "book_in_printing" -> stock.printedQty + quantity
                    else -> stock.printedQty
                }

                val updatedOtherInQty = when (transactionType) {
                    "book_in_donation" -> stock.otherInQty + quantity
                    else -> stock.otherInQty
                }

                val updatedOtherOutQty = when (transactionType) {
                    "book_out_donation" -> stock.otherOutQty + quantity
                    else -> stock.otherOutQty
                }

                // Create a map of updates to apply
                val updates = mapOf(
                    "stock_qty" to updatedStockQty,
                    "sold_qty" to updatedSoldQty,
                    "printed_qty" to updatedPrintedQty,
                    "other_in_qty" to updatedOtherInQty,
                    "other_out_qty" to updatedOtherOutQty
                )

                // Update the stock quantities in the database
                stockRef.updateChildren(updates).addOnSuccessListener {
                    // Stock quantities updated successfully
                }.addOnFailureListener { e ->
                    // Handle failure
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    fun updateDiscrepancy(isbn: String, discrepancy: Int) {
        val stockRef = reference.child(isbn).child("stock")

        stockRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val stock = dataSnapshot.getValue(Stock::class.java) ?: Stock()

                val updatedStock = stock.stockQty + discrepancy

                val updates = mapOf("stock_qty" to updatedStock)

                // Update the stock quantities in the database
                stockRef.updateChildren(updates).addOnSuccessListener {
                    // Stock quantities updated successfully
                }.addOnFailureListener { e ->
                    // Handle failure
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled
            }
        })
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
                    val stockQty = childSnapshot.child("stock").child("stock_qty").getValue(Long::class.java)
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

    fun getTotalValue(): MutableLiveData<Long?> {
        val totalValue = MutableLiveData<Long?>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sumValue: Long = 0
                for (childSnapshot in dataSnapshot.children) {
                    val stockQty = childSnapshot.child("stock").child("stock_qty").getValue(Long::class.java)
                    val sellPrice = childSnapshot.child("book").child("sell_price").getValue(Long::class.java)
                    stockQty?.let { sumValue += (it * sellPrice!!) }
                }
                totalValue.postValue(sumValue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        return totalValue
    }

    fun getTotalSalesQuantity(): MutableLiveData<Long?> {
        val totalSalesQty = MutableLiveData<Long?>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sumSalesQty: Long = 0
                for (childSnapshot in dataSnapshot.children) {
                    val salesQty = childSnapshot.child("stock").child("sold_qty").getValue(Long::class.java)
                    salesQty?.let { sumSalesQty += it }
                }
                totalSalesQty.postValue(sumSalesQty)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        return totalSalesQty
    }

    fun getTotalSales(): MutableLiveData<Long?> {
        val totalSales = MutableLiveData<Long?>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sumValue: Long = 0
                for (childSnapshot in dataSnapshot.children) {
                    val soldQty = childSnapshot.child("stock").child("sold_qty").getValue(Long::class.java)
                    val sellPrice = childSnapshot.child("book").child("sell_price").getValue(Long::class.java)
                    soldQty?.let { sumValue += (it * sellPrice!!) }
                }
                totalSales.postValue(sumValue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        return totalSales
    }
}