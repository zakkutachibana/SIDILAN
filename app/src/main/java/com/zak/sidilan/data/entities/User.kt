package com.zak.sidilan.data.entities

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    val id: String,
    @JvmField @PropertyName("display_name")
    val displayName: String?,
    val email: String?,
    @JvmField @PropertyName("photo_url")
    val photoUrl: String?,
) : Parcelable {
    constructor() : this("","", "", "")
}