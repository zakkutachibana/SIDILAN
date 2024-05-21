package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Book(
    val isbn: Long,
    val title: String,
    val authors: List<String>,
    @JvmField @PropertyName("cover_url")
    var coverUrl: @RawValue Any?,
    val genre: String,
    @JvmField @PropertyName("published_date")
    val publishedDate: String,
    @JvmField @PropertyName("print_price")
    val printPrice: Long,
    @JvmField @PropertyName("sell_price")
    val sellPrice: Long,
    @JvmField @PropertyName("is_perpetual")
    val isPerpetual: Boolean,
    @JvmField @PropertyName("start_contract_date")
    val startContractDate: String? = "",
    @JvmField @PropertyName("end_contract_date")
    val endContractDate: String? = "",

    ) : Parcelable {
    constructor() : this(0, "", listOf(), "", "", "", 0, 0, false, "", "")
}


@Parcelize
data class BookDetail(
    val book: Book?,
    val logs: Logs?,
    val stock: Stock?
) : Parcelable {
    constructor() : this(null, null, null)
}

@Parcelize
data class BookQtyPrice(
    val book: Book,
    var bookQty: Long,
    var bookPrice: Long
) : Parcelable

