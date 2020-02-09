package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Category
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.CategoryFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category_with_logo.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */

class CategoryLogoListAdapter(
    private val dataList: ArrayList<Category>,
    private val selectedData: MutableList<Category>
) :
    RecyclerView.Adapter<CategoryLogoListAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_with_logo, parent, false)
        return CategoryViewHolder(
            view
        )
    }

    /*
    on the categories list (for example on the full store list),
     when you click on an item, it deactivate the one you clicked on.
     the expected behavior is : it deactivate all other categories,
     not the one you clicked on.
     */
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = dataList[position]

        Glide.with(holder.view.context)
             .load(category.icon_url)
             .into(holder.view.iv_store_category_item_image)

        if (selectedData.contains(category))
            holder.view.iv_store_category_item_image.alpha = 1.0f
        else
            holder.view.iv_store_category_item_image.alpha = 0.25f

        holder.view.setOnClickListener {
            val selectAllCategories = selectedData.contains(category) && selectedData.size == 1

            selectedData.clear()
            if (selectAllCategories) {
                selectedData.addAll(dataList)
            } else {
                selectedData.add(category)
            }

            RxBus.publish(CategoryFilterSelectedEvent(selectedData))
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = dataList.size
}