package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by S.Nur Uysal on 2019-11-05.
 */

@Parcelize
data class SelectedOption(
    var country: Country?,
    var city: City?,
    var type: Type?,
    var distance: Long?,
    var searchText: String?
) : Parcelable
