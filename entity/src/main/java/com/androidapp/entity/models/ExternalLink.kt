package com.androidapp.entity.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExternalLink(
    val slotId: String,
    val toName: String,
    val toId: String,
    val slotName: String
) : Parcelable