package com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class MallPageState {
    abstract val pageNum: Int
    abstract val loadedAllItems: Boolean
}

data class DefaultState(override val pageNum: Int, override val loadedAllItems: Boolean) : MallPageState()
data class LoadingState(override val pageNum: Int, override val loadedAllItems: Boolean) : MallPageState()
data class PaginatingState(override val pageNum: Int, override val loadedAllItems: Boolean) : MallPageState()
data class ErrorState(val errorMessage: String, override val pageNum: Int, override val loadedAllItems: Boolean) :
    MallPageState()