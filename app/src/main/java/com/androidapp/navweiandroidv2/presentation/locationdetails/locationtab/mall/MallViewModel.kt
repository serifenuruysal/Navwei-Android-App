package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.mall

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.GetCategoryUseCase
import com.androidapp.domain.mall.SliderUseCase
import com.androidapp.domain.mall.StoresUseCase
import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.entity.models.Slider
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
class MallViewModel @Inject constructor(
    private val storesUseCase: StoresUseCase,
    private val categoryUseCase: GetCategoryUseCase,
    private val sliderUseCase: SliderUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler,
    @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<MallPageState>()

    init {
        stateLiveData.value =
            DefaultState(
                false,
                null,
                emptyList(),
                emptyList(),
                arrayListOf(),
                mutableListOf(),
                listOf()
            )
    }

    @SuppressLint("CheckResult")
    fun getStoresByLocationId(locationId:String) {
        storesUseCase.getStoresByMallId(locationId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getCategories() {
        categoryUseCase.getCategories()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onCategoryResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getSliders(locationId:String) {
        sliderUseCase.getSlider(locationId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onSliderResponseReceived, this::onError)
    }

    private fun onSliderResponseReceived(sliders: List<Slider>) {
        stateLiveData.value =
            DefaultState(
                true,
                obtainSelectedFilters(),
                obtainStoreListData(),
                obtainStoreListData(),
                obtainCategoryListData(),
                obtainSelectedCategoryListData(),
                sliders
            )
    }

    private fun onLocationResponseReceived(locationList: List<Locations>) {
        stateLiveData.value =
            DefaultState(
                true,
                obtainSelectedFilters(),
                locationList,
                locationList,
                obtainCategoryListData(),
                obtainSelectedCategoryListData(),
                obtainSliderListData()
            )
    }

    private fun onCategoryResponseReceived(categoryList: List<Category>) {
        val selectedCategoryList: ArrayList<Category> = arrayListOf()
        selectedCategoryList.addAll(categoryList)
        val selectedFilters = SelectedFilters(null, selectedCategoryList, false,null)

        stateLiveData.value =
            DefaultState(
                true,
                selectedFilters,
                obtainStoreListData(),
                obtainStoreListData(),
                ArrayList(categoryList),
                selectedCategoryList,
                obtainSliderListData()
            )
    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainSelectedFilters(),
                obtainStoreListData(),
                emptyList(),
                obtainCategoryListData(),
                obtainSelectedCategoryListData(),
                obtainSliderListData()
            )
    }

    private fun getSelectedFilters(): SelectedFilters? { return obtainSelectedFilters() }

    private fun obtainCategoryListData() = stateLiveData.value?.categoryList ?: arrayListOf()

    private fun obtainSelectedFilters() = stateLiveData.value?.selectedFilters

    private fun obtainStoreListData() = stateLiveData.value?.locationList ?: emptyList()

    private fun obtainSliderListData() = stateLiveData.value?.sliderList ?: emptyList()

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false

    private fun obtainSelectedCategoryListData() =
        stateLiveData.value?.categorySelectedList ?: mutableListOf()

}