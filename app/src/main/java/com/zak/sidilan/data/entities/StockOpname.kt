package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockOpname (

    val books: BookOpname?,
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
    @JvmField @PropertyName("is_appropriate")
    val isAppropriate: Boolean?,
    val discrepancy: Int?,
    ) : Parcelable {
    constructor() : this(0L, null, false, 0)
}