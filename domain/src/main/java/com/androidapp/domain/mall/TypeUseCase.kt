package com.androidapp.domain.mall

import com.androidapp.entity.models.Type
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class TypeUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    fun getTypes(): Observable<List<Type>> {
        val typeList: MutableList<Type> = mutableListOf()
        return repository.getTypes().map { response ->
            if (response.types.isNotEmpty()) {
                response.types.forEach {
                    if (it.active)
                        typeList.add(it)
                }
            }
        }.map { typeList.toList() }.toObservable()

    }


}