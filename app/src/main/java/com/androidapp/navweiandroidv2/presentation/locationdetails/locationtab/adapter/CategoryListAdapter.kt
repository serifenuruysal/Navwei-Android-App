package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Category
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.CategoryClickEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */

class CategoryListAdapter(
    private val dataList: ArrayList<Category>
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = dataList[position]

        holder.view.tv_offer_item_title.text = category.name
        Glide.with(holder.view.context)
             .load(category.picture_url)
             .into(holder.view.tv_offer_item_image)

        holder.view.setOnClickListener {
            RxBus.publish(CategoryClickEvent(category))
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = dataList.size
}