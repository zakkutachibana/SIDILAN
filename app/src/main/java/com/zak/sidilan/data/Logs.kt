package com.zak.sidilan.data

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Logs(
    @JvmField @PropertyName("created_by")
    val createdBy: String,
    @JvmField @PropertyName("created_at")
    val createdAt: MutableMap<String, String>,
) : Parcelable
