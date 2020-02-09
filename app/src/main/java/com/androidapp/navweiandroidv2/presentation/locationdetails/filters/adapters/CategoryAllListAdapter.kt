package com.androidapp.navweiandroidv2.presentation.locationdetails.filters.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Category
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.CategoryFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category_with_title.view.*

/**
 * Created by S.Nur Uysal on 2019-11-11.
 */

class CategoryAllListAdapter(
    private val MyDateSet: ArrayList<Category>,
    private val selectedData: MutableList<Category>
) :
    RecyclerView.Adapter<CategoryAllListAdapter.AllCategoryViewHolder>() {

    class AllCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAllListAdapter.AllCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_with_title, parent, false)
        return AllCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllCategoryViewHolder, position: Int) {
        var category = MyDateSet.get(position)
        Glide.with(holder.view.context).load(category.icon_url)
            .into(holder.view.tv_offer_item_logo)
        holder.view.tv_filtre_floor_item_title.text = category.name
        holder.view.setOnClickListener {

            if (selectedData.contains(category)) {
                if (selectedData.size > 1 ) {
                    selectedData.remove(category)
                }
            } else if (!selectedData.contains(category)) {
                selectedData.add(category)
            }

            RxBus.publish(CategoryFilterSelectedEvent(selectedData))
            notifyDataSetChanged()
        }

        if (selectedData.contains(category)) {
            holder.view.iv_filters_category_seleted_icon.visibility = View.VISIBLE
        } else {
            holder.view.iv_filters_category_seleted_icon.visibility = View.GONE
        }
    }


    override fun getItemCount() = MyDateSet.size
}