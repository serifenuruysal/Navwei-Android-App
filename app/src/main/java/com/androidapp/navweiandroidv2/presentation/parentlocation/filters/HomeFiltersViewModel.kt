package com.androidapp.navweiandroidv2.presentation.parentlocation.filters

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.filters.GetCityUseCase
import com.androidapp.domain.filters.GetCountryUseCase
import com.androidapp.entity.models.City
import com.androidapp.entity.models.Country
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-11-05.
 */
class HomeFiltersViewModel @Inject constructor(
    private val getCityUseCase: GetCityUseCase,
    private val getCountryUseCase: GetCountryUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler,
    @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<ChooseStoreFiltersState>()

    init {
        stateLiveData.value = LoadingState(
            emptyList(),
            emptyList(),
            "All",
            emptyList(),
            emptyList()
        )
    }

    @SuppressLint("CheckResult")
    fun getCountries() {
        getCountryUseCase.getCountries()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onCountryResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getCities() {
        getCityUseCase.getCities()
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onCityResponseReceived, this::onError)
    }

    private fun onCountryResponseReceived(countryList: List<Country>) {
        val extendedCountryList = mutableListOf<Country>()
        extendedCountryList.add(Country("All"))
        extendedCountryList.addAll(countryList)

        stateLiveData.value =
            LoadingState(
                obtainCategoryListData(),
                extendedCountryList,
                obtainSelectedCountryName(),
                obtainCityListData(), obtainFilteredCityListData()
            )

        stateLiveData.value =
            DefaultState(
                obtainCategoryListData(),
                obtainCountryListData(),
                obtainSelectedCountryName(),
                obtainCityListData(),
                getFilteredCityList(listOf())
            )
    }

    private fun onCityResponseReceived(cityList: List<City>) {
        stateLiveData.value =
            DefaultState(
                obtainCategoryListData(),
                obtainCountryListData(),
                obtainSelectedCountryName(),
                cityList,
                getFilteredCityList(cityList)
            )
    }

    private fun getFilteredCityList(cityList: List<City>): List<City> {
        val filteredCityList = mutableListOf<City>()
        val selectedCountryName = obtainSelectedCountryName()

        filteredCityList.add(City("All"))
        if(selectedCountryName == "All"){
            return filteredCityList
        }

        cityList.forEach { city ->
            if (city.country_name == selectedCountryName) {
                filteredCityList.add(city)
            }
        }

        return filteredCityList
    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCategoryListData(),
                obtainSelectedCountryName(),
                obtainCountryListData(),
                obtainCityListData(), obtainFilteredCityListData()
            )
    }

    fun updateCityList(countryName: String) {
        stateLiveData.value =
            LoadingState(
                obtainCategoryListData(),
                obtainCountryListData(),
                countryName,
                obtainCityListData(),
                obtainFilteredCityListData()
            )

        stateLiveData.value =
            UpdateState(
                obtainCategoryListData(),
                obtainCountryListData(),
                countryName,
                obtainCityListData(),
                getFilteredCityList(obtainCityListData())
            )
    }

    private fun obtainCountryListData() = stateLiveData.value?.countryList ?: emptyList()

    private fun obtainCityListData() = stateLiveData.value?.cityList ?: emptyList()

    private fun obtainFilteredCityListData() = stateLiveData.value?.filteredCityList ?: emptyList()

    private fun obtainCategoryListData() = stateLiveData.value?.categoryList ?: emptyList()

    private fun obtainSelectedCountryName() = stateLiveData.value?.selectedCountryName

}