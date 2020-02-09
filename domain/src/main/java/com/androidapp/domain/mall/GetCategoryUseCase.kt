package com.androidapp.domain.mall

import com.androidapp.entity.models.Category
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
class GetCategoryUseCase @Inject constructor(private val repository: NavweiRepository) {
    fun getCategories(): Observable<List<Category>> {
        val categoryList: MutableList<Category> = mutableListOf()
        return repository.getCategories().map { response ->
            if (response.categories.isNotEmpty()) {
                response.categories.forEach {
                    if (it.active)
                        categoryList.add(it)
                }
            }
        }.map { categoryList.sortedWith(compareBy { it.weight }).reversed() }.toObservable()
    }
}
