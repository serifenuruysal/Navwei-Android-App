package com.androidapp.navweiandroidv2.presentation.locationdetails.filters

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.GetCategoryUseCase
import com.androidapp.domain.map.FloorUseCase
import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Floor
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-11-08.
 */
class FilterViewModel @Inject constructor(
    private val floorUseCase: FloorUseCase,
    private val categoryUseCase: GetCategoryUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<FilterPageState>()

    init {
        stateLiveData.value = DefaultState(false, emptyList(), emptyList())
    }

    @SuppressLint("CheckResult")
    fun getFloors(locationId:String) {
        floorUseCase.getFloorsByMallId(locationId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onFloorsResposneReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getCategories() {
        categoryUseCase.getCategories()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onCategoryResponseReceived, this::onError)
    }

    private fun onFloorsResposneReceived(floorList: List<Floor>) {
        val extendFloorList= mutableListOf<Floor>()

        extendFloorList.add(Floor("All",null,null,0))
        extendFloorList.addAll(floorList)
        stateLiveData.value = DefaultState(obtainCategoryListData().isNotEmpty()&&floorList.isNotEmpty(), extendFloorList, obtainCategoryListData())
    }

    private fun onCategoryResponseReceived(categoryList: List<Category>) {
        stateLiveData.value = DefaultState(obtainFloorListData().isNotEmpty()&&categoryList.isNotEmpty(), obtainFloorListData(), categoryList)
    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainFloorListData(),
                obtainCategoryListData()
            )
    }


    private fun obtainCategoryListData() = stateLiveData.value?.categoryList ?: emptyList()

    private fun obtainFloorListData() = stateLiveData.value?.floorList ?: emptyList()

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false


}