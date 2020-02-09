package com.androidapp.domain.filters

import com.androidapp.entity.models.Country
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-11-04.
 */

class GetCountryUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    fun getCountries(): Observable<List<Country>> {
        return repository.getCountries().map { response ->
            response.countries
        }.toObservable()

    }


}