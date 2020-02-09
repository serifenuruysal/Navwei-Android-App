package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.dijkstra

/**
 * Created by S.Nur Uysal on 2019-12-23.
 */

class NoSuchPathException(s: String?) : Exception(s) {
    constructor() : this(null)
}