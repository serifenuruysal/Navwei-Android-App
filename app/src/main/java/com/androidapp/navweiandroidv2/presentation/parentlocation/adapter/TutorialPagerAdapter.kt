package com.androidapp.navweiandroidv2.presentation.parentlocation.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.androidapp.navweiandroidv2.R

/**
 * Created by S.Nur Uysal on 2020-01-07.
 */
class TutorialPagerAdapter (private val context: Context, private val imageDrawableArrayList: MutableList<Int>) : PagerAdapter() {
    private val inflater: LayoutInflater


    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imageDrawableArrayList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.item_tutorial, view, false)!!

        val imageView = imageLayout
            .findViewById(R.id.iv_tutorial) as ImageView


        imageView.setImageResource(imageDrawableArrayList[position])

        view.addView(imageLayout, 0)

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

}