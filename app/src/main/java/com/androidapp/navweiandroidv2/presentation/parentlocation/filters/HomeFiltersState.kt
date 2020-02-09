package com.androidapp.navweiandroidv2.presentation.parentlocation.filters

import com.androidapp.entity.models.Category
import com.androidapp.entity.models.City
import com.androidapp.entity.models.Country

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class ChooseStoreFiltersState {
    abstract val categoryList: List<Category>
    abstract val countryList: List<Country>
    abstract val selectedCountryName: String?
    abstract val cityList: List<City>
    abstract val filteredCityList: List<City>
}

data class LoadingState(
    override val categoryList: List<Category>,
    override val countryList: List<Country>,
    override val selectedCountryName: String?,
    override val cityList: List<City>,
    override val filteredCityList: List<City>
) : ChooseStoreFiltersState()

data class DefaultState(
    override val categoryList: List<Category>,
    override val countryList: List<Country>,
    override val selectedCountryName: String?,
    override val cityList: List<City>,
    override val filteredCityList: List<City>
) : ChooseStoreFiltersState()

data class UpdateState(
    override val categoryList: List<Category>,
    override val countryList: List<Country>,
    override val selectedCountryName: String?,
    override val cityList: List<City>,
    override val filteredCityList: List<City>
) : ChooseStoreFiltersState()

data class ErrorState(
    val errorMessage: String,
    override val categoryList: List<Category>,
    override val selectedCountryName: String?,
    override val countryList: List<Country>,
    override val cityList: List<City>,
    override val filteredCityList: List<City>
) : ChooseStoreFiltersState()
