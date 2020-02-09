package com.androidapp.domain.offers

import android.annotation.SuppressLint
import com.androidapp.entity.models.Voucher
import io.reactivex.Observable
import repository.NavweiRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class VouchersUseCase @Inject constructor(
    private val repository: NavweiRepository
) {
    @SuppressLint("CheckResult")
    fun getAllVouchersByMallId(locationId: String): Observable<List<Voucher>> {
        return repository.getAllVouchersByMallId(locationId).map { response ->
            getFilteredActiveList(response.voucher)
        }.toObservable()

    }

    @SuppressLint("CheckResult")
    fun getVoucherByStoreId(locationId: String): Observable<List<Voucher>> {
        return repository.getVoucherByStoreId(locationId).map { response ->
            getFilteredActiveList(response.voucher)
        }.toObservable()

    }

    private fun getFilteredActiveList(inList: List<Voucher>): List<Voucher> {
        val voucherList: MutableList<Voucher> = mutableListOf()
        if (inList.isNotEmpty()) {
            inList.forEach {
                if (it.active && checkForValidDate(it) && it.categories != null && it.categories!!.isNotEmpty()) {
                    voucherList.add(it)
                }
            }
        }

        return voucherList
    }

    private fun checkForValidDate(voucher: Voucher): Boolean {
        return voucher.expired_at?.checkForCurrentDate()!!
    }
}

private fun String.checkForCurrentDate(): Boolean {

    val dateFormatI = "yyyy-MM-dd'T'HH:mm:ss"
    val formatInput = SimpleDateFormat(dateFormatI)

    val date = formatInput.parse(this)
    val currentDate = Calendar.getInstance().time
    if (date!! >= currentDate) {
        return true
    }

    return false


}



