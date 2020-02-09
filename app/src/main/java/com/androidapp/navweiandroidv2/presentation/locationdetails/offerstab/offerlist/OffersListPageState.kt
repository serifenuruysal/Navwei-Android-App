package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerlist

import com.androidapp.entity.models.Voucher

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class OffersListPageState {
    abstract val loadedAllItems: Boolean
    abstract val voucherList: List<Voucher>?
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val voucherList: List<Voucher>
) : OffersListPageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val voucherList: List<Voucher>
) : OffersListPageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val voucherList: List<Voucher>
) : OffersListPageState()