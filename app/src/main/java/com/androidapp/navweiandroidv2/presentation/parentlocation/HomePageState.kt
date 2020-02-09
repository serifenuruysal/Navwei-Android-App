package com.androidapp.navweiandroidv2.presentation.parentlocation

import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.Type

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class HomePageState {
    abstract val loadedAllItems: Boolean
    abstract val locationList: List<Locations>
    abstract val filteredLocationList: List<Locations>
    abstract val typeList: List<Type>
}

data class DefaultState(
    override val loadedAllItems: Boolean,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val typeList: List<Type>
) : HomePageState()

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val typeList: List<Type>
) : HomePageState()

data class UpdateState(
    override val loadedAllItems: Boolean,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val typeList: List<Type>
) : HomePageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val locationList: List<Locations>,
    override val filteredLocationList: List<Locations>,
    override val typeList: List<Type>
) : HomePageState()