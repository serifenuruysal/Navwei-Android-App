package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by S.Nur Uysal on 2019-12-27.
 */


@Parcelize
data class NodeModel(val locations: Locations?, val node:Node, val nodeLocation:NodeLocation?, val floor: Floor?): Parcelable