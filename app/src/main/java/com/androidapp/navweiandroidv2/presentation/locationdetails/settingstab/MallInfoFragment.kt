package com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidapp.entity.models.Locations
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseFragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.mall_fragment.toolbar
import kotlinx.android.synthetic.main.mall_info_fragment.*
import kotlinx.android.synthetic.main.mall_info_fragment.app_bar_image
import kotlinx.android.synthetic.main.mall_info_fragment.appbar
import kotlinx.android.synthetic.main.mall_info_fragment.cl_open_close_hours
import kotlinx.android.synthetic.main.mall_info_fragment.cl_schedule_time
import kotlinx.android.synthetic.main.mall_info_fragment.iv_arrow_down
import kotlinx.android.synthetic.main.mall_info_fragment.tv_mall_description
import kotlinx.android.synthetic.main.mall_info_fragment.tv_mall_name
import kotlinx.android.synthetic.main.mall_info_fragment.tv_mall_name_sub_title
import kotlinx.android.synthetic.main.mall_info_fragment.tv_mall_shop_count

/**
 * Created by S.Nur Uysal on 2019-11-14.
 */
class MallInfoFragment : BaseFragment() {

    private var selectedLocation: Locations? = null
    private var isScheduleViewOpen = false

    companion object {
        fun newInstance(
            selectedLocation: Locations?
        ): MallInfoFragment {
            val f = MallInfoFragment()

            val args = Bundle()
            args.putParcelable("SelectedLocation", selectedLocation)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedLocation = arguments?.getParcelable<Locations>("SelectedLocation")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mall_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            //  Vertical offset == 0 indicates appBar is fully  expanded.
            if (Math.abs(verticalOffset) ==0) {
                iv_mall_logo.visibility = View.VISIBLE
            } else {
                iv_mall_logo.visibility = View.GONE
            }
        })

        fillPageData()
        initListener()
    }

    private fun fillPageData() {
        selectedLocation?.let {
            cl_open_close_hours.visibility = View.GONE
            tv_mall_name.text = it.location_details?.name
            tv_mall_name_sub_title.text = it.city_name
            tv_mall_shop_count.text = getString(R.string.title_shops_count, it.nb_store.toString())
            tv_mall_description.text = it.location_details?.description

            Glide.with(this).load(it.location_details?.picture_url)
                .into(app_bar_image)

            Glide.with(this).load(it.location_details?.logo_url)
                .into(iv_mall_logo)


//            tv_mall_name_close_time.text=
        }

    }
    private fun initListener(){
        cl_schedule_time.setOnClickListener {
            if (isScheduleViewOpen) {
                cl_open_close_hours.visibility = View.GONE
                iv_arrow_down.setImageResource(R.drawable.ic_chevron_down_black)
            } else {
                iv_arrow_down.setImageResource(R.drawable.ic_chevron_up)
                cl_open_close_hours.visibility = View.VISIBLE
            }
            isScheduleViewOpen = !isScheduleViewOpen
        }
    }


}