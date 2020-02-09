package com.androidapp.domain.map

import android.annotation.SuppressLint
import com.androidapp.entity.models.Floor
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class FloorUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    @SuppressLint("CheckResult")
    fun getFloorsByMallId(locationId: String): Observable<MutableList<Floor>> {
        val floorList: MutableList<Floor> = mutableListOf()

        return repository.getFloorsByMallId(locationId).map { response ->

            if (response.locations.isNotEmpty()) {
                for (weightIndex in 0 until response.locations.size) {
                    response.locations.forEach {
                        if (it.active) {
                            if (weightIndex == it.weight)
                                floorList.add(
                                    weightIndex,
                                    Floor(
                                        it.location_details?.name!!,
                                        it.location_details?.short_name!!,
                                        it.id!!,
                                        it.weight!!
                                    )
                                )
                        }
                    }
                }

            }


        }.map { floorList }.toObservable()


    }
}