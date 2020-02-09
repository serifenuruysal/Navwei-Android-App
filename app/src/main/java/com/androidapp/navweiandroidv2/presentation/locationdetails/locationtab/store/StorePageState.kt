package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store

import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.Voucher

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class StorePageState {
    abstract val loadedAllItems: Boolean
    abstract val selectedMall: Locations?
    abstract val voucherList: ArrayList<Voucher>?
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val voucherList: ArrayList<Voucher>
) : StorePageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val voucherList: ArrayList<Voucher>
) : StorePageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val voucherList: ArrayList<Voucher>
) : StorePageState()