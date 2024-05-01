package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class User (
    val id: String,
    @JvmField @PropertyName("display_name")
    val displayName: String?,
    val role: String?,
    val email: String?,
    @JvmField @PropertyName("photo_url")
    val photoUrl: String?,
    @JvmField @PropertyName("phone_number")
    val phoneNumber: String?,
    @JvmField @PropertyName("joined_at")
    val joinedAt: @RawValue Any?,
) : Parcelable {
    constructor() : this("","", "", "", "", "", null)
}

@Parcelize
data class Whitelist (
    val email: String?,
    val role: String?,
    @JvmField @PropertyName("phone_number")
    val phoneNumber: String?,
) : Parcelable {
    constructor() : this("","", "")
}