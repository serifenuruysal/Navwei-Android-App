package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab

import android.annotation.SuppressLint
import android.util.Log
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

class OffersViewModel @Inject constructor(
    private val categoryUseCase: GetCategoryUseCase,
    private val allVoucherUseCase: VouchersUseCase,
    private val storesUseCase: StoresUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<OffersPageState>()

    init {
        stateLiveData.value =
            LoadingState(
                false, null, null, arrayListOf(),
                emptyList(), emptyList(), emptyList()
            )
    }


    @SuppressLint("CheckResult")
    fun getCategories() {
        categoryUseCase.getCategories()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onCategoryResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getAllVouchers(storeId: String) {
        allVoucherUseCase.getAllVouchersByMallId(storeId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onVoucherResponseReceived, this::onVoucherError)
    }

    @SuppressLint("CheckResult")
    fun getStoresByLocationId(selectedMall: Locations) {

        stateLiveData.value =
            LoadingState(
                true,
                selectedMall,
                obtainSelectedFilters(),
                obtainCategoryListData(),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                obtainLocationListData()
            )

        storesUseCase.getStoresByMallId(selectedMall.id!!)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getVoucherByStoreId(storeId: String) {
        allVoucherUseCase.getVoucherByStoreId(storeId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onVoucherResponseReceived, this::onVoucherError)
    }

    private fun onLocationResponseReceived(list: List<Locations>) {
        val locationList: MutableList<Locations> = mutableListOf()
        list.forEach { location ->
            obtainVoucherListData().forEach { voucher ->
                if (location.id == voucher.location_id) {
                    locationList.add(location)
                }
            }
        }
        stateLiveData.value =
            LoadingState(
                true,
                obtainSelectedMall(),
                obtainSelectedFilters(),
                obtainCategoryListData(),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                locationList
            )

       getAllVouchers(obtainSelectedMall()?.id!!)
    }

    private fun onVoucherResponseReceived(voucherList: List<Voucher>) {

        val filteredVoucherList: MutableList<Voucher> = mutableListOf()
        voucherList.forEach {
            if (checkForCategoryFilter(it) && checkForSearchFilter(it)) {
                filteredVoucherList.add(it)
            }
        }

        stateLiveData.value =
            DefaultState(
                true,
                obtainSelectedMall(),
                obtainSelectedFilters(),
                obtainCategoryListData(),
                voucherList,
                filteredVoucherList,
                obtainLocationListData()
            )

    }

    private fun checkForSearchFilter(voucher: Voucher): Boolean {
        val searchText = obtainSelectedFilters()?.searchText ?: return true
        if (voucher.name?.contains(searchText,ignoreCase = true)!!) {
            return true
        }
        if (obtainLocationListData().isNotEmpty()) {
            obtainLocationListData().forEach {
                if (it.location_details?.name?.contains(searchText.trim())!!) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkForCategoryFilter(voucher: Voucher): Boolean {
        if (obtainSelectedCategoryListData().isEmpty()) return true
        obtainSelectedCategoryListData().forEach { category -> voucher.categories?.forEach { if (category.id == it.id) return true } }
        return false
    }

    private fun onCategoryResponseReceived(categoryList: List<Category>) {

        val selectedCategoryList: ArrayList<Category> = arrayListOf()
        selectedCategoryList.addAll(categoryList)
        val selectedFilters = SelectedFilters(null, selectedCategoryList, false, null)


        stateLiveData.value =
            LoadingState(
                true,
                obtainSelectedMall(),
                selectedFilters,
                ArrayList(categoryList),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                obtainLocationListData()
            )
    }

    private fun onError(error: Throwable) {
      Log.d("onError",error.localizedMessage)
    }

    private fun onVoucherError(error: Throwable){
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainSelectedMall(),
                obtainSelectedFilters(),
                obtainCategoryListData(),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                obtainLocationListData()
            )
    }


    fun setSelectedFilters(selectedFilters: SelectedFilters?) {
        stateLiveData.value =
            LoadingState(
                true,
                obtainSelectedMall(),
                selectedFilters,
                obtainCategoryListData(),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                obtainLocationListData()
            )

    }

    fun updateCategoryList(categoryList: MutableList<Category>) {
        val selectedFilters = obtainSelectedFilters()
        selectedFilters?.selectedCategoryList = categoryList
        stateLiveData.value =
            LoadingState(
                false,
                obtainSelectedMall(),
                selectedFilters,
                obtainCategoryListData(),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                obtainLocationListData()
            )
        onVoucherResponseReceived(obtainVoucherListData())
    }

    fun searchAtStoreList(text: String) {
        val selectedFilters = obtainSelectedFilters()
        selectedFilters?.searchText = text

        stateLiveData.value =
            LoadingState(
                false,
                obtainSelectedMall(),
                selectedFilters,
                obtainCategoryListData(),
                obtainVoucherListData(),
                obtainFilteredVoucherListData(),
                obtainLocationListData()
            )

        onVoucherResponseReceived(obtainVoucherListData())

    }

    fun getSelectedFilters(): SelectedFilters? {
        return obtainSelectedFilters()
    }

    private fun obtainVoucherListData() =
        stateLiveData.value?.voucherList ?: arrayListOf()

    private fun obtainFilteredVoucherListData() =
        stateLiveData.value?.filteredVoucherList ?: arrayListOf()

    private fun obtainSelectedCategoryListData() =
        stateLiveData.value?.selectedFilters?.selectedCategoryList ?: mutableListOf()

    private fun obtainCategoryListData() = stateLiveData.value?.categoryList ?: arrayListOf()

    private fun obtainSelectedFilters() = stateLiveData.value?.selectedFilters

    private fun obtainSelectedMall() = stateLiveData.value?.selectedMall

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false

    private fun obtainLocationListData() = stateLiveData.value?.locationList ?: arrayListOf()

}