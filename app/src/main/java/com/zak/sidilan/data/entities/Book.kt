package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.errorprone.annotations.Keep
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String,
    val isbn: Long,
    val title: String,
    val authors: List<String>,
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
    @JvmField @PropertyName("stock_qty")
    val stockQty: Long? = 0,

) : Parcelable {
    constructor() : this("", 0, "", listOf(), "", "", 0, 0, false, "", "", 0)
}
