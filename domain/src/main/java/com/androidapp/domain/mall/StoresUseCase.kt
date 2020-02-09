package com.androidapp.domain.mall

import com.androidapp.entity.models.Locations
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class StoresUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    fun getStoresByMallId(locationId: String): Observable<List<Locations>> {
        return repository.getStoresByMallId(locationId).map { response ->
            getFilteredActiveList(response.locations)
        }.toObservable()

    }

    fun getStoresByFloorId(floorId: String): Observable<List<Locations>> {
        return repository.getStoresByFloorId(floorId).map { response ->
            getFilteredActiveList(response.locations)
        }.toObservable()

    }

    fun getStoresByStoreId(storeId: String): Observable<Locations> {
        var responseLocation: Locations? = null
        return repository.getStoresByStoreId(storeId).map { response ->
            if (response.location.active) {
                responseLocation = response.location
            }
        }.map { responseLocation!! }.toObservable()

    }

    private fun getFilteredActiveList(inList: List<Locations>): List<Locations> {
        val locationList: MutableList<Locations> = mutableListOf()
        if (inList.isNotEmpty()) {
            inList.forEach {
                if (it.active && it.categories != null && it.categories!!.isNotEmpty()) {
                    locationList.add(it)
                }
            }
        }

        return locationList
    }


}