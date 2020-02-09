package com.androidapp.navweiandroidv2.presentation.parentlocation.events

import com.androidapp.entity.models.*

/**
 * Created by S.Nur Uysal on 2019-11-06.
 */

data class ClickFiltersEvent(
    val listType: ArrayList<Type>,
    val listMall: ArrayList<Locations>,
    val selectedOption: SelectedOption
)

data class TypeSelectedEvent(val type: Type)

data class CitySelectedEvent(val city: City)

data class CountrySelectedEvent(val country: Country)

data class OnClickMallSelectedEvent(val mall: Locations)
