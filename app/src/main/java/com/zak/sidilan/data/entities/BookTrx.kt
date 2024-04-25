package com.zak.sidilan.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class BookTrx {
    abstract val type: String
    abstract val books: Map<String, Long>
    abstract val totalBooksQty: Long
    abstract val notes: String
}

@Parcelize
data class BookInPrintingTrx(
    override val type: String = "book_in_printing",
    val printingShop: String,
    val bookInDate: String,
    override val books: Map<String, Long>, // Map of book ID to quantity
    override val totalBooksQty: Long,
    val totalCost: Double,
    val isCustomCost: Boolean,
    override val notes: String
) : Parcelable, BookTrx()

@Parcelize
data class BookInDonationTrx(
    override val type: String = "book_in_donation",
    val donorName: String,
    val bookInDate: String,
    override val books: Map<String, Long>, // Map of book ID to quantity
    override val totalBooksQty: Long,
    override val notes: String
) : Parcelable, BookTrx()
@Parcelize

data class BookOutSellingTrx(
    override val type: String = "book_out_sell",
    val buyerName: String,
    val bookOutDate: String,
    val sellingPlatform: String,
    override val books: Map<String, Long>, // Map of book ID to quantity
    override val totalBooksQty: Long,
    val totalPrice: Double,
    val isCustomPrice: Boolean,
    override val notes: String
) : Parcelable, BookTrx()
@Parcelize

data class BookOutDonationTrx(
    override val type: String = "book_out_donation",
    val doneeName: String,
    val bookOutDate: String,
    override val books: Map<String, Long>, // Map of book ID to quantity
    override val totalBooksQty: Long,
    override val notes: String
) : Parcelable, BookTrx()
