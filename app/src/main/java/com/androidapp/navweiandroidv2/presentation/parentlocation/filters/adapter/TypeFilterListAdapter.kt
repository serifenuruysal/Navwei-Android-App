package com.androidapp.navweiandroidv2.presentation.parentlocation.filters.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Type
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.TypeSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import kotlinx.android.synthetic.main.item_filter_type.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */

class TypeFilterListAdapter(
    private val dataList: MutableList<Type>
) :
    RecyclerView.Adapter<TypeFilterListAdapter.TypeListViewHolder>() {
    private var selectedType: Type? = null
    private var isAllSelected = true

    class TypeListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TypeFilterListAdapter.TypeListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter_type, parent, false)
        return TypeListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeListViewHolder, position: Int) {
        val type = dataList[position]
        holder.view.tv_fragment_country_mall.text = type.name

        if (type == selectedType || isAllSelected)
            holder.view.tv_fragment_country_mall.setBackgroundResource(R.drawable.ic_dark_rectangle)
        else
            holder.view.tv_fragment_country_mall.setBackgroundResource(R.drawable.ic_light_rectangle)

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

