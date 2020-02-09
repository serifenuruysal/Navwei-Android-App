package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerlist

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.offers.VouchersUseCase
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_IO
import com.androidapp.navweiandroidv2.di.module.SCHEDULER_MAIN_THREAD
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class OffersListViewModel @Inject constructor(
    private val allVoucherUseCase: VouchersUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<OffersListPageState>()

    init {
        stateLiveData.value =
            LoadingState(
                false, emptyList()
            )
    }


    @SuppressLint("CheckResult")
    fun getVoucherByStoreId(storeId: String) {
        allVoucherUseCase.getVoucherByStoreId(storeId)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onVoucherResponseReceived, this::onVoucherError)
    }


    private fun onVoucherResponseReceived(voucherList: List<Voucher>) {


        stateLiveData.value =
            DefaultState(
                true,
                voucherList
            )

    }


    private fun onVoucherError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainVoucherListData()
            )
    }

    private fun obtainVoucherListData() =
        stateLiveData.value?.voucherList ?: arrayListOf()


    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false


}