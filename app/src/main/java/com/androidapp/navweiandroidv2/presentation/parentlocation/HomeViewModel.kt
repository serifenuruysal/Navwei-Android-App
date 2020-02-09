package com.androidapp.navweiandroidv2.presentation.parentlocation

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.MallUseCase
import com.androidapp.domain.mall.TypeUseCase
import com.androidapp.entity.models.*
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
class HomeViewModel @Inject constructor(
    private val storeUseCase: MallUseCase,
    private val getTypeUseCase: TypeUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler,
    @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<HomePageState>()

    init {
        stateLiveData.value = DefaultState(false, emptyList(), emptyList(), emptyList())
    }

    @SuppressLint("CheckResult")
    fun getMalls() {
        storeUseCase.getLocationOfMalls()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getTypes() {
        getTypeUseCase.getTypes()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onTypeResponseReceived, this::onError)
    }

    private fun onLocationResponseReceived(locationList: List<Locations>) {
        stateLiveData.value =
            DefaultState(
                obtainCurrentLoadedAllItems(),
                locationList,
                locationList,
                obtainTypeListData()
            )

        getTypes()
    }

    private fun onTypeResponseReceived(typeList: List<Type>) {
        val filteredTypedList = mutableListOf<Type>()
        typeList.forEach {
            if (isTypeHasChild(it)) {
                filteredTypedList.add(it)
            }
        }

        stateLiveData.value =
            DefaultState(
                obtainCurrentLoadedAllItems(),
                obtainMallListData(),
                obtainFilterMallList(),
                filteredTypedList
            )
    }

    private fun isTypeHasChild(type: Type): Boolean {
        obtainMallListData().forEach {
            if (it.type?.id == type.id) {
                return true
            }
        }

        return false
    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainMallListData(),
                emptyList(),
                obtainTypeListData()
            )
    }

    fun filterMallList(selectedOption: SelectedOption) {
        val filteredLocationList: MutableList<Locations> = mutableListOf()

        DefaultState(
            obtainCurrentLoadedAllItems(),
            obtainMallListData(),
            obtainFilterMallList(),
            obtainTypeListData()
        )

        obtainMallListData().forEach { locations: Locations ->
            if (checkForName(selectedOption.searchText, locations) && checkForCountry(
                    locations,
                    selectedOption.country
                ) && checkForCity(
                    locations,
                    selectedOption.city
                ) && checkForType(locations, selectedOption.type)
            ) {
                filteredLocationList.add(locations)
            }
        }

        stateLiveData.value =
            UpdateState(true, obtainMallListData(), filteredLocationList, obtainTypeListData())
    }

    private fun checkForType(locations: Locations, type: Type?): Boolean {
        if (type?.name == "All") return true

        return type?.id.equals(locations.type?.id)
    }

    private fun checkForCountry(locations: Locations, country: Country?): Boolean {
        if (country == null)
            return true
        if (country.name == "All")
            return true
        return country.country_code == locations.country_code
    }

    private fun checkForCity(locations: Locations, city: City?): Boolean {
        if (city == null)
            return true
        if (city.name == "All")
            return true
        return city.name == locations.city_name
    }

    private fun checkForName(text: String?, it: Locations): Boolean {
        if (text == null) return true

        return it.location_details?.name?.contains(text.trim(), ignoreCase = true)!!
    }

    fun obtainTypeListData() = stateLiveData.value?.typeList ?: emptyList()

    fun obtainMallListData() = stateLiveData.value?.locationList ?: emptyList()

    private fun obtainCurrentLoadedAllItems() =
        (stateLiveData.value?.locationList?.isNotEmpty()!! && stateLiveData.value?.typeList?.isNotEmpty()!!)

    private fun obtainFilterMallList() =
        stateLiveData.value?.filteredLocationList ?: emptyList()
}