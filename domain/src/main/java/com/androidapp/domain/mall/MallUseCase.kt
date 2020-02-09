package com.androidapp.domain.mall

import com.androidapp.entity.models.Locations
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class MallUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    fun getLocationOfMalls(): Observable<List<Locations>> {
        val locationList: MutableList<Locations> = mutableListOf()
        return repository.getMallLocations().map { response ->
            if (response.locations.isNotEmpty()) {
                response.locations.forEach {
                    if (it.active && it.location_details?.picture_url != null)
                        locationList.add(it)
                }
            }
        }.map { locationList.toList() }.toObservable()
    }
}
