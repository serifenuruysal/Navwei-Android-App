package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.storelist

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.GetCategoryUseCase
import com.androidapp.domain.mall.StoresUseCase
import com.androidapp.domain.offers.VouchersUseCase
import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class StoreListViewModel @Inject constructor(
    private val storesUseCase: StoresUseCase,
    private val categoryUseCase: GetCategoryUseCase,
    private val allVoucherUseCase: VouchersUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<MallCategoryPageState>()

    init {
        stateLiveData.value =
            LoadingState(
                false,
                null,
                obtainSelectedFilters(),
                emptyList(),
                emptyList(),
                arrayListOf(),
                listOf()
            )
    }

    @SuppressLint("CheckResult")
    fun getStoresByMallId(locationId: String) {

        storesUseCase.getStoresByMallId(locationId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getStoresByFloorId(floorId: String) {
        storesUseCase.getStoresByFloorId(floorId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getCategories(locationId: String) {
        stateLiveData.value =
            LoadingState(
                obtainCurrentLoadedAllItems(),
                locationId,
                obtainSelectedFilters(),
                obtainStoreListData(),
                obtainFilteredStoreListData(),
                obtainCategoryListData(),
                obtainVoucherListData()
            )

        categoryUseCase.getCategories()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onCategoryResponseReceived, this::onError)
    }

    private fun onLocationResponseReceived(locationList: List<Locations>) {
        val filteredLocationList: MutableList<Locations> = mutableListOf()

        locationList.forEach {
            if (checkForCategoryFilter(it) &&
                checkForStoresWithOffer(it) &&
                checkForSearchFilter(it)
            ) {

                filteredLocationList.add(it)
            }
        }

        stateLiveData.value =
            DefaultState(
                true,
                obtainLocationId(),
                obtainSelectedFilters(),
                locationList,
                filteredLocationList,
                obtainCategoryListData(),
                obtainVoucherListData()
            )
    }

    @SuppressLint("CheckResult")
    fun getAllVouchers() {
        obtainLocationId()?.let {
            allVoucherUseCase.getAllVouchersByMallId(obtainLocationId()!!)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .subscribe(this::onVoucherResponseReceived, this::onError)
        }

    }

    private fun onVoucherResponseReceived(voucherList: List<Voucher>) {
        stateLiveData.value =
            LoadingState(
                true,
                obtainLocationId(),
                obtainSelectedFilters(),
                obtainStoreListData(),
                obtainFilteredStoreListData(),
                obtainCategoryListData(),
                voucherList
            )

    }

    private fun onCategoryResponseReceived(categoryList: List<Category>) {
        if (obtainSelectedCategoryListData().isEmpty()) {
            val selectedFilters = obtainSelectedFilters()
            selectedFilters.selectedCategoryList?.addAll(categoryList)
        }
        stateLiveData.value =
            LoadingState(
                true,
                obtainLocationId(),
                obtainSelectedFilters(),
                obtainStoreListData(),
                obtainStoreListData(),
                ArrayList(categoryList),
                obtainVoucherListData()
            )

        getStoresByMallId(obtainLocationId()!!)
    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainLocationId(),
                obtainSelectedFilters(),
                obtainStoreListData(),
                emptyList(),
                obtainCategoryListData(),
                obtainVoucherListData()
            )
    }


    fun updateCategoryList(categoryList: MutableList<Category>) {
        val selectedFilters = obtainSelectedFilters()
        selectedFilters.selectedCategoryList = categoryList

        stateLiveData.value =
            LoadingState(
                false,
                obtainLocationId(),
                selectedFilters,
                obtainStoreListData(),
                obtainStoreListData(),
                obtainCategoryListData(),
                obtainVoucherListData()
            )
        onLocationResponseReceived(obtainStoreListData())
    }

    fun setSelectedFilters(selectedFilters: SelectedFilters?) {
        stateLiveData.value =
            LoadingState(
                false,
                obtainLocationId(),
                selectedFilters,
                obtainStoreListData(),
                obtainFilteredStoreListData(),
                obtainCategoryListData(),
                obtainVoucherListData()
            )

    }

    fun getLocationsWithSelectedFloor() {
        if (obtainSelectedFilters().selectedFloor != null) {
            if (obtainSelectedFilters().selectedFloor?.floorId != null) {
                getStoresByFloorId(obtainSelectedFilters().selectedFloor?.floorId!!)
            } else {
                obtainLocationId()?.let { getStoresByMallId(it) }
            }
        }
    }

    private fun checkForSearchFilter(location: Locations): Boolean {
        val searchText = obtainSelectedFilters().searchText ?: return true

        if (location.location_details?.name?.contains(
                searchText.trim(),
                ignoreCase = true
            )!!
        ) {
            return true
        }

        return false
    }

    private fun checkForCategoryFilter(location: Locations): Boolean {
        if (obtainSelectedCategoryListData().isEmpty())
            return true

        obtainSelectedCategoryListData().forEach { category ->
            location.categories?.forEach {
                if (category.id == it.id)
                    return true
            }
        }

        return false
    }

    private fun checkForStoresWithOffer(
        location: Locations
    ): Boolean {
        if (!stateLiveData.value?.selectedFilters?.isStoreSwitchOn!!) return true

        obtainVoucherListData().forEach {
            if (it.location_id == location.id) return true
        }
        return false
    }

    fun searchAtStoreList(text: String) {
        val selectedFilters = obtainSelectedFilters()
        selectedFilters.searchText = text

        stateLiveData.value =
            LoadingState(
                false,
                obtainLocationId(),
                selectedFilters,
                obtainStoreListData(),
                obtainFilteredStoreListData(),
                obtainCategoryListData(),
                obtainVoucherListData()
            )

        onLocationResponseReceived(obtainStoreListData())
    }

    private fun obtainVoucherListData() = stateLiveData.value?.voucherList ?: mutableListOf()

    private fun obtainSelectedCategoryListData() =
        obtainSelectedFilters().selectedCategoryList ?: mutableListOf()

    private fun obtainCategoryListData() = stateLiveData.value?.categoryList ?: arrayListOf()

    private fun obtainSelectedFilters() =
        stateLiveData.value?.selectedFilters ?: SelectedFilters(null, mutableListOf(), false, null)

    private fun obtainStoreListData() = stateLiveData.value?.locationList ?: emptyList()

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false

    private fun obtainFilteredStoreListData() =
        stateLiveData.value?.filteredLocationList ?: emptyList()

    private fun obtainLocationId() = stateLiveData.value?.locationId
}