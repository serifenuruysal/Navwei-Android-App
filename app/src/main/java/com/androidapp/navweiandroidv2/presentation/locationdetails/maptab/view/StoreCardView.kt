package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.androidapp.entity.models.NodeModel
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.StoreAdapterType
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.CloseStoreCardEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OnClickLocationSelectedEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OpenStorePageEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapterType
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_store_map_card.view.*

/**
 * Created by S.Nur Uysal on 2019-12-25.
 */
class StoreCardView : RelativeLayout {

    private var selectedStore: NodeModel? = null
    private var vouchers: ArrayList<Voucher>? = arrayListOf()

    lateinit var view: View

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initListeners() {
        view.tv_navigate_to_store.setOnClickListener {
            RxBus.publish(
                OnClickLocationSelectedEvent(
                    selectedStore!!,
                    StoreAdapterType.DESTINATION
                )
            )
        }

        view.tv_locate_on_map_button.setOnClickListener {
            RxBus.publish(
                OpenStorePageEvent(
                    selectedStore!!.locations
                )
            )
        }
    }

    private fun initView() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        view = inflater.inflate(R.layout.view_store_map_card, this, true)
        initListeners()
    }

    fun setStoreVoucherData(vouchers: ArrayList<Voucher>){
        this.vouchers = vouchers
        if (vouchers.isNotEmpty()) fillVoucherList()
    }

    fun setStoreData(selectedStore: NodeModel?) {
        this.selectedStore = selectedStore

        view.tv_mall_store_title.text = selectedStore?.locations?.location_details?.name

        Glide.with(context!!)
            .load(selectedStore?.locations?.location_details?.picture_url)
            .into(view.iv_location_image)

        Glide.with(context!!)
            .load(selectedStore?.locations?.location_details?.logo_url)
            .into(view.iv_store_logo)

        if (vouchers!!.isNotEmpty()) fillVoucherList()

        iv_close_button.setOnClickListener {
            RxBus.publish(CloseStoreCardEvent())
        }

        selectedStore?.locations?.let {
            val storeName = selectedStore.locations?.categories!![0].name
            val floorName = selectedStore.floor?.floorName
            val finalName = "$storeName • $floorName"

            view.tv_store_sub_title.text = finalName
        }
    }

    private fun fillVoucherList() {
        rv_store_offers.adapter = OffersListAdapter(
            vouchers!!, false,
            OffersListAdapterType.MAP
        )
    }

}