package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab

import com.androidapp.entity.models.*

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

sealed class MapPageState {
    abstract val loadedAllItems: Boolean
    abstract val mallId: String?
    abstract val floorList: List<Floor>?
    abstract val selectedFloor: Floor?
    abstract val floorMapList: MutableList<FloorMap>
    abstract val nodeModelList: MutableList<NodeModel>?
    abstract val locationList: List<Locations>?
    abstract val floorMapMap: HashMap<Floor, FloorMap>
    abstract val voucherList: ArrayList<Voucher>
}

data class LoadingState(
    override val loadedAllItems: Boolean,
    override val mallId: String?,
    override val floorList: List<Floor>?,
    override val selectedFloor: Floor?,
    override val floorMapList: MutableList<FloorMap>,
    override val nodeModelList: MutableList<NodeModel>?,
    override val locationList: List<Locations>?,
    override val floorMapMap: HashMap<Floor, FloorMap>,
    override val voucherList: ArrayList<Voucher>
) :
    MapPageState()

data class MapDataLoadedState(
    override val loadedAllItems: Boolean,
    override val mallId: String?,
    override val floorList: List<Floor>?,
    override val selectedFloor: Floor?,
    override val floorMapList: MutableList<FloorMap>,
    override val nodeModelList: MutableList<NodeModel>?,
    override val locationList: List<Locations>?,
    override val floorMapMap: HashMap<Floor, FloorMap>,
    override val voucherList: ArrayList<Voucher>
) :MapPageState()


data class VouchersUpdateState(
    override val loadedAllItems: Boolean,
    override val mallId: String?,
    override val floorList: List<Floor>?,
    override val selectedFloor: Floor?,
    override val floorMapList: MutableList<FloorMap>,
    override val nodeModelList: MutableList<NodeModel>,
    override val locationList: List<Locations>?,
    override val floorMapMap: HashMap<Floor, FloorMap>,
    override val voucherList: ArrayList<Voucher>
) :
    MapPageState()

data class ErrorState(
    val errorMessage: String,
    override val mallId: String?,
    override val loadedAllItems: Boolean,
    override val floorList: List<Floor>?,
    override val selectedFloor: Floor?,
    override val floorMapList: MutableList<FloorMap>,
    override val nodeModelList: MutableList<NodeModel>?,
    override val locationList: List<Locations>?,
    override val floorMapMap: HashMap<Floor, FloorMap>,
    override val voucherList: ArrayList<Voucher>
) :
    MapPageState()
data class MapErrorState(
    val errorMessage: String,
    override val mallId: String?,
    override val loadedAllItems: Boolean,
    override val floorList: List<Floor>?,
    override val selectedFloor: Floor?,
    override val floorMapList: MutableList<FloorMap>,
    override val nodeModelList: MutableList<NodeModel>?,
    override val locationList: List<Locations>?,
    override val floorMapMap: HashMap<Floor, FloorMap>,
    override val voucherList: ArrayList<Voucher>
) :
    MapPageState()