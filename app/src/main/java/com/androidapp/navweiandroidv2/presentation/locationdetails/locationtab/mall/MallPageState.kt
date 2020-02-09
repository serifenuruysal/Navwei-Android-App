package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.mall

import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.entity.models.Slider

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class MallPageState {
    abstract val loadedAllItems: Boolean
    abstract val selectedFilters: SelectedFilters?
    abstract val locationList: List<Locations>
    abstract val filteredLocationList: List<Locations>
    abstract val categoryList: ArrayList<Category>
    abstract val categorySelectedList: MutableList<Category>?
    abstract val sliderList: List<Slider>
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val selectedFilters: SelectedFilters?,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val categoryList: ArrayList<Category>,
    override val categorySelectedList: MutableList<Category>?,
    override val sliderList: List<Slider>
) : MallPageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val selectedFilters: SelectedFilters?,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val categoryList: ArrayList<Category>,
    override val categorySelectedList: MutableList<Category>?,
    override val sliderList: List<Slider>
) : MallPageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val selectedFilters: SelectedFilters?,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val categoryList: ArrayList<Category>,
    override val categorySelectedList: MutableList<Category>?,
    override val sliderList: List<Slider>
) : MallPageState()