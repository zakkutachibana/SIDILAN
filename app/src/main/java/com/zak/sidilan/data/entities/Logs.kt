package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Logs(
    @JvmField @PropertyName("created_by")
    val createdBy: String,
    @JvmField @PropertyName("created_at")
    var createdAt: @RawValue Any?,
) : Parcelable {
    constructor() : this("", 0)
}
