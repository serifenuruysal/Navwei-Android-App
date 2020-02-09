package com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events

import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Floor
import com.androidapp.entity.models.SelectedFilters

/**
 * Created by S.Nur Uysal on 2019-11-09.
 */


class OnClickCategoryEvent(val selectedCategoryList: MutableList<Category>)

class OnClickFloorEvent(val selectedFloor: Floor)

class OnClickSwitchStoresWIthOffersEvent(val isTurnedOn: Boolean)

class StoreListFilterSelectedEvent(val selectedFilters: SelectedFilters)

class CategoryFilterSelectedEvent(val selectedCategoryList: MutableList<Category>)

class FloorFilterSelectedEvent(val selectedFloor: Floor)

class CategoryClickEvent(val lastSelectedCategory:Category)
