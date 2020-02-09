package com.androidapp.navweiandroidv2.presentation.parentlocation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Type
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.TypeSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_type.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */

class TypeListAdapter(
    private val dataList: List<Type>
) :
    RecyclerView.Adapter<TypeListAdapter.TypeViewHolder>() {

    private var selectedType: Type? = null
    private var isAllSelected = true

    class TypeViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TypeListAdapter.TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_type, parent, false)
        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        var type = dataList[position]
        holder.view.tv_store_category_item_title.text = type.name
        type.picture_url?.apply {
            Glide.with(holder.view.context).load(type.picture_url)
                .into(holder.view.iv_store_category_item_image)
        }
        if (type == selectedType || isAllSelected)
            holder.view.iv_store_category_item_image.alpha = 1.0f
        else
            holder.view.iv_store_category_item_image.alpha = 0.25f

        holder.view.setOnClickListener {
            type.let {


                if (isAllSelected) {
                    isAllSelected = false
                    selectedType = it
                    RxBus.publish(
                        TypeSelectedEvent(it)
                    )

                } else {
                    if (it == selectedType) {
                        selectAll()
                        RxBus.publish(
                            TypeSelectedEvent(getDefaultType())
                        )
                    } else {
                        isAllSelected = false
                        selectedType = it
                        RxBus.publish(
                            TypeSelectedEvent(it)
                        )
                    }
                }


                notifyDataSetChanged()
            }
        }
    }

    fun setSelected(type: Type) {
        isAllSelected = type.name.equals("All")
        selectedType = type
        notifyDataSetChanged()
    }

    private fun selectAll() {
        isAllSelected = true
        selectedType = getDefaultType()
    }


    override fun getItemCount() = dataList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun getDefaultType(): Type {
        return Type(
            name = "All",
            id = "",
            slug = "",
            picture_url = "",
            icon_url = "",
            active = true
        )
    }
}

