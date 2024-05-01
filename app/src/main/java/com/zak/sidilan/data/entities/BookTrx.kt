package com.zak.sidilan.data.entities

import com.google.firebase.database.PropertyName

data class BookSubtotal(
    val bookId: String,
    val qty: Long,
    val subtotalPrice: Long
)

data class BookInPrintingTrx(
    val type: String = "book_in_printing",
    @JvmField @PropertyName("printing_shop_name")
    val printingShopName: String,
    @JvmField @PropertyName("book_in_date")
    val bookInDate: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    @JvmField @PropertyName("total_cost")
    val totalCost: Long,
    @JvmField @PropertyName("discount_type")
    val discountType: String,
    @JvmField @PropertyName("discount_percent")
    val discountPercent: Long?,
    @JvmField @PropertyName("discount_amount")
    val discountAmount: Long?,
    @JvmField @PropertyName("final_cost")
    val finalCost: Long,
    val notes: String?
)

data class BookInDonationTrx(
    val type: String = "book_in_donation",
    @JvmField @PropertyName("donor_name")
    val donorName: String,
    @JvmField @PropertyName("book_in_date")
    val bookInDate: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    val notes: String
)

data class BookOutSellingTrx(
    val type: String = "book_out_selling",
    @JvmField @PropertyName("buyer_name")
    val buyerName: String,
    @JvmField @PropertyName("book_out_date")
    val bookOutDate: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    @JvmField @PropertyName("total_price")
    val totalPrice: Long,
    @JvmField @PropertyName("discount_type")
    val discountType: String,
    @JvmField @PropertyName("discount_percent")
    val discountPercent: Long?,
    @JvmField @PropertyName("discount_amount")
    val discountAmount: Long?,
    @JvmField @PropertyName("final_price")
    val finalPrice: Long,
    val notes: String?
)

data class BookOutDonationTrx(
    val type: String = "book_out_donation",
    @JvmField @PropertyName("donee_name")
    val doneeName: String,
    @JvmField @PropertyName("book_out_date")
    val bookOutDate: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    val notes: String?
)
