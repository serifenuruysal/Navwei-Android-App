package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.MapStepsModel
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.util.ext.gone
import com.androidapp.navweiandroidv2.util.ext.visible
import kotlinx.android.synthetic.main.item_map_steps.view.*


/**
 * Created by S.Nur Uysal on 2019-11-11.
 */

class MapStepsListAdapter(
    private val dataList: MutableList<MapStepsModel>
) :
    RecyclerView.Adapter<MapStepsListAdapter.AllCategoryViewHolder>() {

    class AllCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MapStepsListAdapter.AllCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_map_steps, parent, false)
        return AllCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllCategoryViewHolder, position: Int) {
        val data = dataList[position]

        holder.view.tv_title.text = data.stepName
        if (data.isBold) {
            val face = ResourcesCompat.getFont(holder.view.context, R.font.cairo_bold)
            holder.view.tv_title.typeface=face
        }else{
            val face = ResourcesCompat.getFont(holder.view.context, R.font.cairo_semibold)
            holder.view.tv_title.typeface=face
        }



        if (data.bitmap != null) {
            holder.view.iv_logo_bitmap.setImageBitmap(data.bitmap)
            holder.view.iv_logo_bitmap.visible()
            holder.view.iv_logo.gone()
            holder.view.iv_logo.setBackground(
                ContextCompat.getDrawable(
                    holder.view.context,
                    R.drawable.shape_white_circle
                )
            )
        } else if (data.resource != null) {
            holder.view.iv_logo.setImageDrawable(holder.view.context.getDrawable(data.resource!!))
            holder.view.iv_logo_bitmap.gone()
            holder.view.iv_logo.visible()
        }


    }

    override fun getItemCount() = dataList.size
}

