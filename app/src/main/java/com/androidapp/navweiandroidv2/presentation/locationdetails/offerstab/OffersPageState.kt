package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab

import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.entity.models.Voucher

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class OffersPageState {
    abstract val loadedAllItems: Boolean
    abstract val selectedMall:Locations?
    abstract val selectedFilters: SelectedFilters?
    abstract val categoryList: ArrayList<Category>
    abstract val voucherList: List<Voucher>?
    abstract val filteredVoucherList: List<Voucher>?
    abstract val locationList: List<Locations>?
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val selectedFilters: SelectedFilters?,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>,
    override val filteredVoucherList: List<Voucher>,
    override val locationList: List<Locations>
) : OffersPageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val selectedFilters: SelectedFilters?,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>,
    override val filteredVoucherList: List<Voucher>,
    override val locationList: List<Locations>
) : OffersPageState()
data class UpdateState(
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val selectedFilters: SelectedFilters?,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>,
    override val filteredVoucherList: List<Voucher>,
    override val locationList: List<Locations>
) : OffersPageState()
data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val selectedMall: Locations?,
    override val selectedFilters: SelectedFilters?,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>,
    override val filteredVoucherList: List<Voucher>,
    override val locationList: List<Locations>
) : OffersPageState()