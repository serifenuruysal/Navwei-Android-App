package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Locations
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickStoreEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickStoreToMapEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.helper.ScheduleDateHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_mall.view.*
import kotlinx.android.synthetic.main.item_mall.view.iv_store_item_image
import kotlinx.android.synthetic.main.item_store.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */

class StoreListAdapter(
    private val MyDataSet: List<Locations>
) :
    RecyclerView.Adapter<StoreListAdapter.MallStoreViewHolder>() {

    class MallStoreViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MallStoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store, parent, false)
        return MallStoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: MallStoreViewHolder, position: Int) {
        val location = MyDataSet[position]
        holder.view.tv_mall_store_title.text = location.location_details?.name

        val categoryName = location.categories?.get(0)?.name
        val floor = location.location_parent_details?.name
        val fullInfo = "$categoryName • $floor"
        holder.view.tv_mall_store_type.text = fullInfo

        val helper = ScheduleDateHelper(location.location_details?.schedule, holder.view.context)
        holder.view.tv_mall_store_open_time.text = helper.getOpenCloseTimeInfo()
        helper.setOpenCloseTimeStatu(holder.view.tv_mall_store_open_close_statu)

        location.location_details?.picture_url.let {
            Glide.with(holder.view.context).load(location.location_details?.logo_url)
                .into(holder.view.iv_store_item_image)
        }
        holder.view.iv_mall_store_go_arrow.setOnClickListener {
            RxBus.publish(OnClickStoreToMapEvent(location))
        }
        holder.view.setOnClickListener {

            location.let {
                Log.d("serife", "RxBus.publish(OnClickStoreEvent")
                RxBus.publish(OnClickStoreEvent(location))
            }
        }
    }

    override fun getItemCount() = MyDataSet.size
}