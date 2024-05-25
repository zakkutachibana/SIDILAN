package com.zak.sidilan.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookOpname (
    val bookDetail: BookDetail,

    ) : Parcelable