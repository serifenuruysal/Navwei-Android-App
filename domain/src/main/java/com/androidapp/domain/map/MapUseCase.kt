package com.androidapp.domain.map

import android.annotation.SuppressLint
import com.androidapp.entity.models.FloorMap
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class MapUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    @SuppressLint("CheckResult")
    fun getMapByFloorId(floorId: String): Observable<FloorMap> {
        return repository.getMapByFloorId(floorId).map { response ->
            response.floorMap!!
        }.toObservable()

    }


}