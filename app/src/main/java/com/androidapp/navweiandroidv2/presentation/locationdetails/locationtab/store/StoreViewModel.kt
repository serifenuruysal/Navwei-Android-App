package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.offers.VouchersUseCase
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class StoreViewModel @Inject constructor(
    private val voucherUseCase: VouchersUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<StorePageState>()

    init {
        stateLiveData.value = LoadingState(false, null, arrayListOf())
    }


    @SuppressLint("CheckResult")
    fun getAllVouchers(location: Locations) {
        stateLiveData.value = LoadingState(obtainCurrentLoadedAllItems(), location, arrayListOf())
        voucherUseCase.getVoucherByStoreId(location.id!!)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onVoucherResponseReceived, this::onError)
    }

    private fun onVoucherResponseReceived(voucherList: List<Voucher>) {

        stateLiveData.value =
            DefaultState(
                true,
                obtainLocation(),
                ArrayList(voucherList)
            )

    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainLocation(),
                obtainVoucherListData()
            )
    }


    private fun obtainVoucherListData() = stateLiveData.value?.voucherList ?: arrayListOf()

    private fun obtainLocation() = stateLiveData.value?.selectedMall

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false

}