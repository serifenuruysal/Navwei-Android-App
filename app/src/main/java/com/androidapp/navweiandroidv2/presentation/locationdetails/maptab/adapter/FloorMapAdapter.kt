package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Floor
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OnClickFloorMapSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import kotlinx.android.synthetic.main.item_map_floor.view.*

/**
 * Created by S.Nur Uysal on 2019-11-11.
 */

class FloorMapAdapter(
    private val dataList: ArrayList<Floor>
) :
    RecyclerView.Adapter<FloorMapAdapter.AllCategoryViewHolder>() {
    private var selectedFloor: Floor? = dataList[0]
    private var enableList: ArrayList<Floor> = dataList

    class AllCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FloorMapAdapter.AllCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_map_floor, parent, false)
        return AllCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllCategoryViewHolder, position: Int) {
        val floor = dataList[position]
        val isEnable = enableList.contains(floor)

        if (floor == selectedFloor) {
            holder.view.view_floor_backround.visibility = View.VISIBLE
            holder.view.tv_filtre_floor_item_title.setTextColor(
                ContextCompat.getColor(
                    holder.view.context,
                    R.color.white
                )
            )
        } else {
            holder.view.view_floor_backround.visibility = View.GONE
            holder.view.tv_filtre_floor_item_title.setTextColor(
                ContextCompat.getColor(
                    holder.view.context,
                    R.color.title_color
                )
            )
        }

        holder.view.isEnabled = isEnable

        if (isEnable) {
            holder.view.tv_filtre_floor_item_title.setTextColor(
                ContextCompat.getColor(
                    holder.view.context,
                    R.color.title_color
                )
            )

        } else {
            holder.view.tv_filtre_floor_item_title.setTextColor(
                ContextCompat.getColor(
                    holder.view.context,
                    R.color.light_text_color
                )
            )

        }

        var title = floor.floorShortName
        if ("parking".equals(floor.floorShortName)) {
            title = "P"
        }
        holder.view.tv_filtre_floor_item_title.text = title
        holder.view.setOnClickListener {
            if (isEnable) {
                selectedFloor = floor
                RxBus.publish(OnClickFloorMapSelectedEvent(floor))
                notifyDataSetChanged()
            }
        }
    }
    fun setSelected(floor:Floor){
        selectedFloor=floor
        notifyDataSetChanged()
    }

    fun setEnableList(enableList: ArrayList<Floor>) {
        this.enableList = enableList
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataList.size
}