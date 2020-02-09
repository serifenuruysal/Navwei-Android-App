package com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.StoresUseCase
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class MoreViewModel @Inject constructor(
    private val storesUseCase: StoresUseCase, @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(
        SCHEDULER_MAIN_THREAD
    ) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<MallPageState>()

    init {
        stateLiveData.value = DefaultState(0, false)
    }


}