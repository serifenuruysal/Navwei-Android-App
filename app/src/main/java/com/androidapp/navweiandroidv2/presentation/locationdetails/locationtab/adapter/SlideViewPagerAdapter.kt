package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.androidapp.entity.models.SliderItem
import com.androidapp.navweiandroidv2.R
import com.bumptech.glide.Glide

/**
 * Created by S.Nur Uysal on 2019-11-13.
 */
class SlideViewPagerAdapter(
    private val mContext: Context,
    private val itemList: MutableList<SliderItem>
) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(R.layout.item_slide_view, collection, false) as ViewGroup
        val slideView = layout.findViewById<ImageView>(R.id.iv_slide_view)

        itemList[position].picture_url?.let {
            Glide.with(mContext).load(it).into(slideView)
        }

        layout.setOnClickListener {
            // TODO: Implement action
        }

        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}
