package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockOpname (

    val books: List<BookOpname>?,
    val date: String?,
    val logs: Logs?

    ) : Parcelable {
    constructor() : this(null,"",null)
}

@Parcelize
data class BookOpname (
    val isbn: Long?,
    @JvmField @PropertyName("book_title")
    val bookTitle: String?,
    @JvmField @PropertyName("cover_url")
    val coverUrl: String?,
    @JvmField @PropertyName("stock_expected")
    val stockExpected: Long?,
    @JvmField @PropertyName("stock_actual")
    var stockActual: Long?,
    @JvmField @PropertyName("is_appropriate")
    var isAppropriate: Boolean?,
    var discrepancy: Int?,
    var reason : String?
    ) : Parcelable {
    constructor() : this(0L, null, "",0L, 0L,false, 0, "")
}