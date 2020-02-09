package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidapp.domain.mall.StoresUseCase
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
class OfferDetailViewModel @Inject constructor(
    private val voucherUseCase: VouchersUseCase,
    private val storesUseCase: StoresUseCase,
    @Named(SCHEDULER_IO) val subscribeOnScheduler: Scheduler, @Named(SCHEDULER_MAIN_THREAD) val observeOnScheduler: Scheduler
) : ViewModel() {

    val stateLiveData = MutableLiveData<OfferDetailPageState>()

    init {
        stateLiveData.value = LoadingState(false, null, arrayListOf(), listOf())
    }

    @SuppressLint("CheckResult")
    fun getVouchersOfSameStore(voucher: Voucher) {
        stateLiveData.value = LoadingState(
            obtainCurrentLoadedAllItems(),
            voucher,
            obtainVoucherListData(),
            obtainLocationListData()
        )

        voucherUseCase.getVoucherByStoreId(voucher.location_id!!)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onVoucherResponseReceived, this::onError)
    }

    @SuppressLint("CheckResult")
    fun getStoreByVoucher(voucher: Voucher) {
        stateLiveData.value = LoadingState(
            obtainCurrentLoadedAllItems(),
            voucher,
            obtainVoucherListData(),
            obtainLocationListData()
        )
        storesUseCase.getStoresByStoreId(voucher.location_id!!)
            .subscribeOn(subscribeOnScheduler)
            .observeOn(observeOnScheduler)
            .subscribe(this::onLocationResponseReceived, this::onError)
    }

    private fun onLocationResponseReceived(location: Locations) {
        val locationList: MutableList<Locations> = mutableListOf()
        locationList.add(location)

        stateLiveData.value =
            DefaultState(
                true,
                obtainSelectedVoucher(),
                obtainVoucherListData(),
                locationList
            )
    }

    private fun onVoucherResponseReceived(voucherList: List<Voucher>) {
        val currentVoucherList: MutableList<Voucher> = mutableListOf()
        voucherList.forEach {
            if (obtainSelectedVoucher()?.id != it.id)
                currentVoucherList.add(it)
        }
        stateLiveData.value =
            DefaultState(
                true,
                obtainSelectedVoucher(),
                ArrayList(currentVoucherList),
                obtainLocationListData()
            )
    }

    private fun onError(error: Throwable) {
        stateLiveData.value =
            ErrorState(
                error.message ?: "",
                obtainCurrentLoadedAllItems(),
                obtainSelectedVoucher(),
                obtainVoucherListData(),
                obtainLocationListData()
            )
    }

    private fun obtainSelectedVoucher() = stateLiveData.value?.selectedVoucher

    private fun obtainLocationListData() = stateLiveData.value?.locationList ?: emptyList()

    private fun obtainVoucherListData() = stateLiveData.value?.voucherList ?: arrayListOf()

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false

}