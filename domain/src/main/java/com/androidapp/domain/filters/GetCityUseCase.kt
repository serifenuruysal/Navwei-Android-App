package com.androidapp.domain.filters

import com.androidapp.entity.models.City
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-11-04.
 */
class GetCityUseCase @Inject constructor(private val repository: NavweiRepository) {
    fun getCities(): Observable<List<City>> {
        return repository.getCities().map { response ->
            response.cities
        }.toObservable()
    }
}
