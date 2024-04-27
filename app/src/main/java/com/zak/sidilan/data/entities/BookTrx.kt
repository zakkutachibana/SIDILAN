package com.zak.sidilan.data.entities

import com.google.firebase.database.PropertyName

sealed class BookTrx {
    abstract val type: String
    abstract val books: List<BookSubtotal>
    abstract val totalBooksQty: Long
    abstract val totalAmount: Long
    abstract val notes: String
}

data class BookSubtotal(
    val bookId: String,
    val qty: Long,
    val subtotalPrice: Long
)

data class BookInPrintingTrx(
    override val type: String = "book_in_printing",
    @JvmField @PropertyName("printing_shop")
    val printingShop: String,
    @JvmField @PropertyName("book_in_date")
    val bookInDate: String,
    override val books: List<BookSubtotal>, // List of book items
    @get:PropertyName("total_books_qty")
    override val totalBooksQty: Long,
    @get:PropertyName("total_amount")
    override val totalAmount: Long, // Total amount for the transaction
    @get:PropertyName("is_custom_cost")
    val isCustomCost: Boolean,
    override val notes: String
) : BookTrx()

data class BookInDonationTrx(
    override val type: String = "book_in_donation",
    val donatorName: String,
    val bookInDate: String,
    override val books: List<BookSubtotal>, // List of book items
    override val totalBooksQty: Long,
    override val totalAmount: Long, // Total amount for the transaction
    override val notes: String
) : BookTrx()

data class BookOutSellingTrx(
    override val type: String = "book_out_sell",
    val buyerName: String,
    val bookOutDate: String,
    val sellingPlatform: String,
    override val books: List<BookSubtotal>, // List of book items
    override val totalBooksQty: Long,
    override val totalAmount: Long, // Total amount for the transaction
    override val notes: String
) : BookTrx()

data class BookOutDonationTrx(
    override val type: String = "book_out_donation",
    val donateeName: String,
    val bookOutDate: String,
    override val books: List<BookSubtotal>, // List of book items
    override val totalBooksQty: Long,
    override val totalAmount: Long, // Total amount for the transaction
    override val notes: String
) : BookTrx()
