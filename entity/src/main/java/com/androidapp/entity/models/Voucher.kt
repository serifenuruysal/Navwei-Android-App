package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Voucher(
    val active: Boolean,
    val categories: List<Category>?,
    val cover_url: String?,
    val expired_at: String?,
    val id: String?,
    val keywords: String?,
    val location_id: String?,
    val name: String?,
    val picture_url: String?
) : Parcelable
