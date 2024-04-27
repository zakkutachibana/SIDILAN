package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    @JvmField @PropertyName("stock_qty")
    val stockQty: Long = 0,
//    @JvmField @PropertyName("sold_qty")
//    val soldQty: Long = 0,
//    @JvmField @PropertyName("printed_qty")
//    val printedQty: Long = 0,
//    @JvmField @PropertyName("other_in_qty")
//    val otherInQty: Long = 0,
//    @JvmField @PropertyName("other_out_qty")
//    val otherOutQty: Long = 0
) : Parcelable {
    constructor() : this (0)
}
