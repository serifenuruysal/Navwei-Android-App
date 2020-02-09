package com.androidapp.navweiandroidv2.presentation.locationdetails.filters

import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Floor

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class FilterPageState {
    abstract val loadedAllItems: Boolean
    abstract val floorList: List<Floor>
    abstract val categoryList: List<Category>
}

data class DefaultState(
    override val loadedAllItems: Boolean, override val floorList: List<Floor>,
    override val categoryList: List<Category>
) : FilterPageState()

data class LoadingState(
    override val loadedAllItems: Boolean, override val floorList: List<Floor>,
    override val categoryList: List<Category>
) : FilterPageState()

data class ErrorState(
    val errorMessage: String,
    override val loadedAllItems: Boolean,
    override val floorList: List<Floor>, override val categoryList: List<Category>
) : FilterPageState()