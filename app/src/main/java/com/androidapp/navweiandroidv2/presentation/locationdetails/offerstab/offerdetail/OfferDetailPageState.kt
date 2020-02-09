package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail

import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.Voucher

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class OfferDetailPageState {
    abstract val loadedAllItems: Boolean
    abstract val selectedVoucher: Voucher?
    abstract val voucherList: ArrayList<Voucher>?
    abstract val locationList: List<Locations>?
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val selectedVoucher: Voucher?,
    override val voucherList: ArrayList<Voucher>,
    override val locationList: List<Locations>
) : OfferDetailPageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val selectedVoucher: Voucher?,
    override val voucherList: ArrayList<Voucher>,
    override val locationList: List<Locations>
) : OfferDetailPageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val selectedVoucher: Voucher?,
    override val voucherList: ArrayList<Voucher>,
    override val locationList: List<Locations>
) : OfferDetailPageState()