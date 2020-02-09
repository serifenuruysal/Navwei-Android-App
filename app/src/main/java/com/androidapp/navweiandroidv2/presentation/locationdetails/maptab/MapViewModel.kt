package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.StoresUseCase
import com.androidapp.domain.map.FloorUseCase
import com.androidapp.domain.map.MapUseCase
import com.androidapp.domain.offers.VouchersUseCase
import com.androidapp.entity.models.*
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class MapViewModel @Inject constructor(
    private val floorUseCase: FloorUseCase,
    private val mapUseCase: MapUseCase,
    private val storesUseCase: StoresUseCase,
    private val voucherUseCase: VouchersUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler,
    @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<MapPageState>()

    init {
        stateLiveData.value = LoadingState(
            false, null, listOf(), null, mutableListOf(), mutableListOf(),
            emptyList(), hashMapOf(), arrayListOf()
        )
    }

    @SuppressLint("CheckResult")
    fun getAllVouchersByLocation(location: Locations) {

        voucherUseCase.getVoucherByStoreId(location.id!!)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onVoucherResponseReceived, this::onError)
    }

    private fun onVoucherResponseReceived(voucherList: List<Voucher>) {

        stateLiveData.value =
            VouchersUpdateState(
                false,
                obtainSelectedMallId(),
                obtainFloorListData(),
                obtainSelectedFloor(),
                obtainMapListData(),
                obtainNodeModelList(),
                obtainLocationList(),
                obtainFloorMapMap(),
                ArrayList(voucherList)
            )

    }


    @SuppressLint("CheckResult")
    private fun getStoresByLocationId(locationId: String) {
        storesUseCase.getStoresByMallId(locationId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getFloors(mallId: String) {
        stateLiveData.value =
            LoadingState(
                false,
                mallId,
                obtainFloorListData(),
                obtainSelectedFloor(),
                obtainMapListData(),
                obtainNodeModelList(),
                obtainLocationList(),
                obtainFloorMapMap(),
                obtainVoucherList()
            )
        floorUseCase.getFloorsByMallId(mallId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onFloorsResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    private fun getMapData(floorId: String) {
        mapUseCase.getMapByFloorId(floorId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onMapDataResponseReceived, this::onMapError)
    }

    private fun onLocationResponseReceived(locationList: List<Locations>) {

        stateLiveData.value =
            LoadingState(
                true,
                obtainSelectedMallId(),
                obtainFloorListData(),
                obtainSelectedFloor(),
                obtainMapListData(),
                obtainNodeModelList(),
                locationList,
                obtainFloorMapMap(),
                obtainVoucherList()
            )

        obtainFloorListData().forEach {
            getMapData(it.floorId.toString())
        }


    }


    private fun findFloor(location: Locations?): Floor? {
        location ?: return null
        val floor: Floor? = null
        obtainFloorListData().forEach {
            if (it.floorId == location?.parent_id)
                return it
        }

        return floor
    }


    private fun findNodeLocation(slot: String?, storeLocations: List<NodeLocation>): NodeLocation? {
        storeLocations.forEach {
            if (slot == it.slot)
                return it
        }
        return null

    }

    private fun findLocation(location: NodeLocation): Locations? {
        obtainLocationList().forEach {
            if (it.id == location.locationId) return it

        }
        return null
    }

    private fun onMapDataResponseReceived(floorMap: FloorMap) {

        val nodeModelList: MutableList<NodeModel> = obtainNodeModelList()

        var floor: Floor? = null
        floorMap.data.nodes?.forEach { node ->
            node.let {
                val nodeLocation = findNodeLocation(node.id, floorMap.data.storeLocations!!)
                var location: Locations? = null
                if (nodeLocation != null)
                    location = findLocation(nodeLocation)

                if (floor == null)
                    floor = findFloor(location)

                nodeModelList.add(
                    NodeModel(
                        location,
                        node,
                        nodeLocation,
                        floor
                    )
                )
            }

        }


        val floorMapList = obtainMapListData()
        floorMapList.add(floorMap)

        if (floorMapList.size == obtainFloorListData().size) {

            val floorMapMap = obtainFloorMapMap()
            floorMapList.forEach { floorMap ->
                floor = findFloor(floorMap.data)
                floorMapMap.put(floor!!, floorMap)
            }

            stateLiveData.value = MapDataLoadedState(
                true,
                obtainSelectedMallId(),
                obtainFloorListData(),
                obtainSelectedFloor(),
                floorMapList,
                nodeModelList,
                obtainLocationList(),
                floorMapMap,
                obtainVoucherList()
            )

        } else {

            stateLiveData.value = LoadingState(
                false,
                obtainSelectedMallId(),
                obtainFloorListData(),
                obtainSelectedFloor(),
                floorMapList,
                nodeModelList,
                obtainLocationList(),
                obtainFloorMapMap(),
                obtainVoucherList()
            )
        }

    }

    private fun findFloor(mapData: MapData): Floor {
        val nodeModelList = obtainNodeModelList()

        mapData.nodes?.forEach { node ->
            nodeModelList.forEach { nodeModel ->
                if (nodeModel.node.id == node.id && nodeModel.floor != null) {
                    return nodeModel.floor!!
                }
            }
        }

        return null!! //   \\__ :)  __//
    }


    private fun onFloorsResponseReceived(floorList: List<Floor>) {

        stateLiveData.value = LoadingState(
            true,
            obtainSelectedMallId(),
            floorList,
            floorList[0],
            obtainMapListData(),
            obtainNodeModelList(),
            obtainLocationList(),
            obtainFloorMapMap(),
            obtainVoucherList()
        )
        getStoresByLocationId(obtainSelectedMallId()!!)

    }
    private fun onMapError(error: Throwable) {
        stateLiveData.value =
            MapErrorState(
                error.message ?: "",
                obtainSelectedMallId(),
                obtainCurrentLoadedAllItems(),
                obtainFloorListData(),
                obtainSelectedFloor(),
                obtainMapListData(),
                obtainNodeModelList(),
                obtainLocationList(),
                obtainFloorMapMap(),
                obtainVoucherList()
            )
    }
    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainSelectedMallId(),
                obtainCurrentLoadedAllItems(),
                obtainFloorListData(),
                obtainSelectedFloor(),
                obtainMapListData(),
                obtainNodeModelList(),
                obtainLocationList(),
                obtainFloorMapMap(),
                obtainVoucherList()
            )
    }

    fun clearVoucherList() {

        stateLiveData.value = LoadingState(
            false,
            obtainSelectedMallId(),
            obtainFloorListData(),
            obtainSelectedFloor(),
            obtainMapListData(),
            obtainNodeModelList(),
            obtainLocationList(),
            obtainFloorMapMap(),
            arrayListOf()
        )

    }

    fun getNodeModeList(): List<NodeModel>? {
        return obtainNodeModelList()
    }

    fun getNodeModelOfLocation(selectedDestination: Locations?): NodeModel? {
        obtainNodeModelList().forEach {
            if (it.locations?.id == selectedDestination?.id)
                return it
        }
        return null
    }


    fun getFloorList(): ArrayList<Floor> = ArrayList(obtainFloorListData())

    private fun obtainFloorListData() = stateLiveData.value?.floorList ?: listOf()

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false

    private fun obtainMapListData() = stateLiveData.value?.floorMapList ?: mutableListOf()

    private fun obtainNodeModelList() = stateLiveData.value?.nodeModelList ?: mutableListOf()

    private fun obtainSelectedFloor() = stateLiveData.value?.selectedFloor

    private fun obtainSelectedMallId() = stateLiveData.value?.mallId

    private fun obtainLocationList() = stateLiveData.value?.locationList ?: emptyList()

    private fun obtainFloorMapMap() = stateLiveData.value?.floorMapMap ?: hashMapOf()

    private fun obtainVoucherList() = stateLiveData.value?.voucherList ?: arrayListOf()


}