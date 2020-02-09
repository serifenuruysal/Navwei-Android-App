package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.androidapp.entity.models.NodeModel
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OnClickLocationSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_map_store.view.*

/**
 * Created by S.Nur Uysal on 2019-11-11.
 */

class MapStoreListAdapter(
    context: Context,
    private val dataSource: List<NodeModel>,
    private val storeAdapterType: StoreAdapterType
) : BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.item_map_store, parent, false)

        val nodeModel = dataSource[position]

        rowView.tv_store_name.text = nodeModel.locations?.location_details?.name
        rowView.tv_floor_name.text = nodeModel.floor?.floorName

        Glide.with(rowView.context).load(nodeModel.locations?.location_details?.logo_url)
            .into(rowView.iv_store_item_image)

        rowView.setOnClickListener {
            RxBus.publish(OnClickLocationSelectedEvent(nodeModel, storeAdapterType))
        }

        return rowView
    }
}

enum class StoreAdapterType {
    SOURCE, DESTINATION
}