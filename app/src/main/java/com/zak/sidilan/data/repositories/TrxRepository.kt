package com.zak.sidilan.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.zak.sidilan.data.entities.BookInDonationTrx
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookOutDonationTrx
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.data.entities.Whitelist
import org.koin.dsl.module

val trxRepositoryModule = module {
    factory { TrxRepository() }
}

class TrxRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("transactions")
    fun addBookInPrintTrx(trx: BookInPrintingTrx, callback: (String, Boolean) -> Unit) {
        val id = reference.push().key ?: ""

        reference.child(id).setValue(trx)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }
    fun addBookInDonationTrx(trx: BookInDonationTrx, callback: (String, Boolean) -> Unit) {
        val id = reference.push().key ?: ""

        reference.child(id).setValue(trx)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }
    fun addBookOutSellTrx(trx: BookOutSellingTrx, callback: (String, Boolean) -> Unit) {
        val id = reference.push().key ?: ""

        reference.child(id).setValue(trx)
            .addOnSuccessListener {
                // Data successfully inserted
                callback("Successfully inserted transaction", true)
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                callback("Error inserting transaction: $e", false)
            }
    }
    fun addBookOutDonationTrx(trx: BookOutDonationTrx, callback: (String, Boolean) -> Unit) {
        val id = reference.push().key ?: ""

        reference.child(id).setValue(trx)
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
}