package com.androidapp.navweiandroidv2.presentation.parentlocation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Locations
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.OnClickMallSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.helper.ScheduleDateHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_mall.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */

class MallListAdapter(
    private val myDataSet: List<Locations>
) :
    RecyclerView.Adapter<MallListAdapter.MallViewHolder>() {

    interface StoreNavigation {
        fun onStoreSelected(store: Locations)
    }

    class MallViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MallViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mall, parent, false)
        return MallViewHolder(view)
    }

    override fun onBindViewHolder(holder: MallViewHolder, position: Int) {
        val location = myDataSet[position]
        holder.view.tv_store_item_title.text = location.location_details?.name

        val helper = ScheduleDateHelper(location.location_details?.schedule, holder.view.context)
        holder.view.tv_open_close_time.text = helper.getOpenCloseTimeInfo()
        helper.setOpenCloseTimeStatu(holder.view.tc_open_close_statu)

        holder.view.tv_store_count.text =
            holder.view.context.getString(R.string.title_shops_count, location.nb_store.toString())

        location.location_details?.picture_url.let {
            Glide.with(holder.view.context).load(location.location_details?.picture_url)
                .into(holder.view.iv_store_item_image)
        }

        holder.view.setOnClickListener {
            location.let {
                RxBus.publish(OnClickMallSelectedEvent(location))
            }
        }
    }

    override fun getItemCount() = myDataSet.size
}

