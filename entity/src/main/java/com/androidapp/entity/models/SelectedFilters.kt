package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by S.Nur Uysal on 2019-11-05.
 */

@Parcelize
data class SelectedFilters(
    var selectedFloor: Floor?,
    var selectedCategoryList: MutableList<Category>?,
    var isStoreSwitchOn: Boolean,
    var searchText: String?
) : Parcelable

@Parcelize
data class Floor(
    var floorName: String,
    var floorShortName: String?,
    var floorId: String?,
    var weight: Int
) : Parcelable

