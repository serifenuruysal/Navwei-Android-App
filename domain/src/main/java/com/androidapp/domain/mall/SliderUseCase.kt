package com.androidapp.domain.mall

import com.androidapp.entity.models.Slider
import io.reactivex.Observable
import repository.NavweiRepository
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
class SliderUseCase @Inject constructor(private val repository: NavweiRepository) {
    fun getSlider(locationId: String): Observable<List<Slider>> {
        val sliderList: MutableList<Slider> = mutableListOf()
        return repository.getSlider(locationId).map { response ->
            if (response.sliders.isNotEmpty()) {
                response.sliders.forEach {
                    if (it.active)
                        sliderList.add(it)
                }
            }
        }.map { sliderList.toList() }.toObservable()
    }
}
