package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.storelist

import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.entity.models.Voucher

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class MallCategoryPageState {
    abstract val loadedAllItems: Boolean
    abstract val locationId: String?
    abstract val selectedFilters: SelectedFilters?
    abstract val locationList: List<Locations>
    abstract val filteredLocationList: List<Locations>
    abstract val categoryList: ArrayList<Category>
    abstract val voucherList: List<Voucher>?
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val locationId: String?,
    override val selectedFilters: SelectedFilters?,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>
) : MallCategoryPageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val locationId: String?,
    override val selectedFilters: SelectedFilters?,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>
) : MallCategoryPageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val locationId: String?,
    override val selectedFilters: SelectedFilters?,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val categoryList: ArrayList<Category>,
    override val voucherList: List<Voucher>
) : MallCategoryPageState()