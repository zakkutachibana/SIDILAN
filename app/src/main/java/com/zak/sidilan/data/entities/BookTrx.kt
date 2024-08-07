package com.zak.sidilan.data.entities

import com.google.firebase.database.PropertyName

sealed class BookTrx {
    abstract var id: String
    abstract val type: String
}
data class BookSubtotal(
    @JvmField @PropertyName("book_id")
    val isbn: Long,
    @JvmField @PropertyName("book_title")
    val bookTitle: String,
    val qty: Long,
    @JvmField @PropertyName("unit_price")
    val unitPrice: Long,
    @JvmField @PropertyName("subtotal")
    val subtotal: Long
) {
    constructor() : this(0L, "", 0L, 0L, 0L)
}

data class BookTrxDetail(
    val bookTrx: BookTrx?,
    val logs: Logs?
) {
    constructor() : this(null, null)
}

data class BookInPrintingTrx(
    override var id: String = "",
    override val type: String = "book_in_printing",
    @JvmField @PropertyName("printing_shop_name")
    val printingShopName: String,
    val address: String,
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
) : BookTrx() {
    constructor() : this("", "", "", "","", listOf(), 0L, 0L, 0L, "", 0L, 0L, 0L, "")
}

data class BookInDonationTrx(
    override var id: String = "",
    override val type: String = "book_in_donation",
    @JvmField @PropertyName("donor_name")
    val donorName: String,
    val address: String,
    @JvmField @PropertyName("book_in_date")
    val bookInDate: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    val notes: String
) : BookTrx() {
    constructor() : this("", "", "", "", "", listOf(), 0L, 0L, "")
}

data class BookOutSellingTrx(
    override var id: String = "",
    override val type: String = "book_out_selling",
    @JvmField @PropertyName("buyer_name")
    val buyerName: String,
    val address: String,
    @JvmField @PropertyName("book_out_date")
    val bookOutDate: String,
    @JvmField @PropertyName("selling_platform")
    val sellingPlatform: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    @JvmField @PropertyName("total_price")
    val totalPrice: Long,
    @JvmField @PropertyName("is_paid")
    val isPaid: Boolean,
    @JvmField @PropertyName("discount_type")
    val discountType: String,
    @JvmField @PropertyName("discount_percent")
    val discountPercent: Long?,
    @JvmField @PropertyName("discount_amount")
    val discountAmount: Long?,
    @JvmField @PropertyName("final_price")
    val finalPrice: Long,
    val notes: String?
) : BookTrx() {
    constructor() : this("", "","", "", "", "", listOf(), 0L, 0L, 0L, false, "",0L, 0L, 0L, "")
}

data class BookOutDonationTrx(
    override var id: String = "",
    override val type: String = "book_out_donation",
    @JvmField @PropertyName("donee_name")
    val doneeName: String,
    val address: String,
    @JvmField @PropertyName("book_out_date")
    val bookOutDate: String,
    val books: List<BookSubtotal>,
    @JvmField @PropertyName("total_book_qty")
    val totalBookQty: Long,
    @JvmField @PropertyName("total_book_kind")
    val totalBookKind : Long,
    val notes: String?
) : BookTrx() {
    constructor() : this("", "","", "", "", listOf(), 0L, 0L, "")
}
