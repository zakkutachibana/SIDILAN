package com.zak.sidilan.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.zak.sidilan.data.entities.BookOpname
import com.zak.sidilan.data.entities.Logs
import com.zak.sidilan.data.entities.StockOpname
import org.koin.dsl.module

val stockOpnameRepositoryModule = module {
    factory { StockOpnameRepository() }
}

class StockOpnameRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("stock_opname")

    fun getAllStockOpname(year: String): MutableLiveData<ArrayList<StockOpname>> {
        val stockOpnameList = MutableLiveData<ArrayList<StockOpname>>()

        reference.child(year).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stockOpnames = ArrayList<StockOpname>()
                for (eachStockOpname in snapshot.children) {
                    val stockOpname = eachStockOpname.getValue(StockOpname::class.java)
                    stockOpname?.let { stockOpnames.add(it) }
                }
                stockOpnameList.value = stockOpnames
                Log.d("SUCCESS GET ALL STOCK OPNAME", "${stockOpnameList.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return stockOpnameList
    }

    fun saveStockOpname(
        date: String,
        books: List<BookOpname>,
        createdBy: String,
        period: String,
        overallAppropriate: Boolean,
        callback: (String) -> Unit
    ) {

        val booksMap = books.associateBy { it.isbn.toString() }
        val createdAt = ServerValue.TIMESTAMP
        val logs = Logs(createdBy, createdAt)
        val stockOpname = StockOpname(period, booksMap, date, overallAppropriate, logs)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                reference.child(period).setValue(stockOpname).addOnSuccessListener {
                    callback("User data added to database successfully")
                }
                    .addOnFailureListener { e ->
                        callback("Error adding user data to database: $e")
                    }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback("Database error: $databaseError")
            }
        })
    }
}

