package com.zak.sidilan.data.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import com.zak.sidilan.data.entities.BookInDonationTrx
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookOutDonationTrx
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.entities.BookTrx
import com.zak.sidilan.data.entities.BookTrxDetail
import com.zak.sidilan.data.entities.Logs
import org.koin.dsl.module
import java.io.File

val trxRepositoryModule = module {
    factory { TrxRepository() }
}

class TrxRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("transactions")

    fun getAllTrx(): MutableLiveData<ArrayList<BookTrxDetail>> {
        val bookTrx = MutableLiveData<ArrayList<BookTrxDetail>>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = ArrayList<BookTrxDetail>()
                for (eachTransaction in snapshot.children) {
                    val type = eachTransaction.child("transaction").child("type").getValue(String::class.java)
                    val transaction = when (type) {
                        "book_in_printing" -> eachTransaction.child("transaction").getValue(BookInPrintingTrx::class.java)
                        "book_in_donation" -> eachTransaction.child("transaction").getValue(BookInDonationTrx::class.java)
                        "book_out_selling" -> eachTransaction.child("transaction").getValue(BookOutSellingTrx::class.java)
                        "book_out_donation" -> eachTransaction.child("transaction").getValue(BookOutDonationTrx::class.java)
                        else -> null
                    }
                    val logs = eachTransaction.child("logs").getValue(Logs::class.java)
                    val trxDetail = BookTrxDetail(transaction, logs)
                    trxDetail.let { transactions.add(it) }

                }
                bookTrx.value = transactions
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return bookTrx
    }

    fun getTrxDetailById(trxId: String): LiveData<BookTrxDetail?> {
        val trxDetailLiveData = MutableLiveData<BookTrxDetail?>()

        reference.child(trxId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val type = snapshot.child("transaction").child("type").getValue(String::class.java)
                val trx = when (type) {
                    "book_in_printing" -> snapshot.child("transaction").getValue(BookInPrintingTrx::class.java)
                    "book_in_donation" -> snapshot.child("transaction").getValue(BookInDonationTrx::class.java)
                    "book_out_selling" -> snapshot.child("transaction").getValue(BookOutSellingTrx::class.java)
                    "book_out_donation" -> snapshot.child("transaction").getValue(BookOutDonationTrx::class.java)
                    else -> null
                }
                val logs = snapshot.child("logs").getValue(Logs::class.java)

                val bookDetail = BookTrxDetail(trx, logs)
                trxDetailLiveData.value = bookDetail
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return trxDetailLiveData
    }

    fun addBookInPrintTrx(trx: BookInPrintingTrx, logs: Logs, callback: (String, Boolean) -> Unit) {
        val id = generateInvoiceId(trx.type)
        trx.id = id
        logs.createdAt = ServerValue.TIMESTAMP

        val trxMap = mutableMapOf<String, Any>()
        trxMap["transaction"] = trx
        trxMap["logs"] = logs

        reference.child(id).setValue(trxMap)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }
    fun addBookInDonationTrx(trx: BookInDonationTrx, logs: Logs, callback: (String, Boolean) -> Unit) {
        val id = generateInvoiceId(trx.type)
        trx.id = id
        logs.createdAt = ServerValue.TIMESTAMP

        val trxMap = mutableMapOf<String, Any>()
        trxMap["transaction"] = trx
        trxMap["logs"] = logs

        reference.child(id).setValue(trxMap)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }
    fun addBookOutSellTrx(trx: BookOutSellingTrx, logs: Logs, callback: (String, Boolean) -> Unit) {
        val id = generateInvoiceId(trx.type)
        trx.id = id
        logs.createdAt = ServerValue.TIMESTAMP

        val trxMap = mutableMapOf<String, Any>()
        trxMap["transaction"] = trx
        trxMap["logs"] = logs

        reference.child(id).setValue(trxMap)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }
    fun addBookOutDonationTrx(trx: BookOutDonationTrx, logs: Logs, callback: (String, Boolean) -> Unit) {
        val id = generateInvoiceId(trx.type)
        trx.id = id
        logs.createdAt = ServerValue.TIMESTAMP

        val trxMap = mutableMapOf<String, Any>()
        trxMap["transaction"] = trx
        trxMap["logs"] = logs

        reference.child(id).setValue(trxMap)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }

    fun updateBookStock(bookId: String, transactionType: String, quantity: Long) {
        // Fetch current stock quantity of the book
        database.reference.child("books").child(bookId).child("stock").child("stock_qty")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentStockQuantity = dataSnapshot.getValue(Long::class.java) ?: 0

                    // Adjust stock quantity based on transaction type
                    val updatedStockQuantity = when (transactionType) {
                        "book_in_printing" -> currentStockQuantity + quantity
                        "book_in_donation" -> currentStockQuantity + quantity
                        "book_out_selling" -> currentStockQuantity - quantity
                        "book_out_donation" -> currentStockQuantity - quantity
                        else -> maxOf(0L, currentStockQuantity - quantity)
                    }
                    // Update stock quantity in the database
                    database.reference.child("books").child(bookId).child("stock")
                        .child("stock_qty").setValue(updatedStockQuantity)
                        .addOnSuccessListener {
                            // Stock quantity updated successfully
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled
                }
            })
    }
    fun saveInvoicePDF(file: File) {
        val storageRef = Firebase.storage.reference.child("invoices/${file.name}")
        storageRef.putFile(Uri.fromFile(file))
            .addOnSuccessListener {
                Log.d("PDF", "PDF invoice uploaded to Firebase Storage.")
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "PDF",
                    "Failed to upload PDF invoice: ${exception.message}",
                    exception
                )
            }
    }


    private fun generateInvoiceId(transactionType: String): String {
        val prefix = when (transactionType) {
            "book_in_printing" -> "INVP"
            "book_in_donation" -> "INVI"
            "book_out_selling" -> "INVS"
            "book_out_donation" -> "INVO"
            else -> "ERR!" // Default prefix
        }

        val sequentialNumber = generateRandomKey(8)

        // Construct the invoice ID by combining prefix, timestamp, and sequential number
        return "$prefix-${sequentialNumber}"
    }

    private fun generateRandomKey(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}