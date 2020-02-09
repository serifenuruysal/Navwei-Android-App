package com.androidapp.navweiandroidv2.presentation.locationdetails.filters.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Floor
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.FloorFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import kotlinx.android.synthetic.main.item_category_with_title.view.tv_filtre_floor_item_title
import kotlinx.android.synthetic.main.item_floor.view.*

/**
 * Created by S.Nur Uysal on 2019-11-11.
 */

class FloorListAdapter(
    private val dataList: ArrayList<Floor>,
    private var selectedFloor: Floor?
) :
    RecyclerView.Adapter<FloorListAdapter.AllCategoryViewHolder>() {

    class AllCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FloorListAdapter.AllCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_floor, parent, false)
        return AllCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllCategoryViewHolder, position: Int) {
        var floor = dataList[position]

        if (floor == selectedFloor) {
            holder.view.iv_filters_category_seleted_icon.visibility = View.VISIBLE
        } else {
            holder.view.iv_filters_category_seleted_icon.visibility = View.GONE
        }

        holder.view.tv_filtre_floor_item_title.text = floor.floorName
        holder.view.setOnClickListener {

            selectedFloor = floor
            RxBus.publish(FloorFilterSelectedEvent(floor))
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = dataList.size
}