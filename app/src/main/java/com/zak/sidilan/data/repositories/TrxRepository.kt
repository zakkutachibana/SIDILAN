package com.zak.sidilan.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookSubtotal
import com.zak.sidilan.data.entities.Whitelist
import org.koin.dsl.module

val trxRepositoryModule = module {
    factory { TrxRepository() }
}
class TrxRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("transactions")
    fun addBookInPrintTrx(trx: BookInPrintingTrx): MutableLiveData<String?> {
        val id = reference.push().key ?: ""

        val status = MutableLiveData<String?>()

        reference.child(id).setValue(trx)
            .addOnSuccessListener {
                // Data successfully inserted
                status.value = "Successfully inserted transaction"
            }
            .addOnFailureListener { e ->
                // Failed to insert data
                status.value = "Error inserting transaction: $e"
            }
        return status
    }
}