package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.androidapp.entity.models.NodeModel
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.StoreAdapterType
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.*
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.gone
import com.androidapp.navweiandroidv2.util.ext.visible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_store_minified_map_card.view.*


/**
 * Created by S.Nur Uysal on 2019-12-24.
 */

class MinifedStoreCardView : RelativeLayout {
    private var isCardOpen = false
    private var totalTime = 0
    lateinit var view: View

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        view = inflater.inflate(R.layout.view_store_minified_map_card, this, true)
    }

    fun showStoreInfoCard(nodeModel: NodeModel) {
        isCardOpen = false


        if (nodeModel.locations != null) {
            iv_mall_store_go_arrow.visible()
            iv_open_close_statu.visible()
            tv_mall_store_open_time.visible()
            tv_min_label.gone()
            Glide.with(context).load(nodeModel.locations?.location_details?.logo_url)
                .into(view.iv_store_item_image)

            view.tv_mall_store_title.text = nodeModel.locations?.location_details?.name

            iv_mall_store_go_arrow.setImageResource(R.drawable.ic_directions_right)
            iv_mall_store_go_arrow.setOnClickListener {
                RxBus.publish(
                    OnClickLocationSelectedEvent(
                        nodeModel,
                        StoreAdapterType.DESTINATION
                    )
                )
            }

            iv_open_close_statu.setImageResource(R.drawable.ic_oval_arrow_up)
            view.setOnClickListener {
                if (isCardOpen) {
                    RxBus.publish(CloseStoreCardEvent())
                } else {
                    RxBus.publish(OpenStoreCardEvent(nodeModel))
                }
                toggelCardOpenStatu()

            }


        } else {
            tv_mall_store_open_time.gone()

            iv_open_close_statu.setImageResource(R.drawable.ic_oval_arrow_down)

            if (nodeModel.node.logo_url != null)
                Glide.with(context).load(nodeModel.node.logo_url)
                    .into(view.iv_store_item_image)

            if (nodeModel.node.externalLink != null && nodeModel.node.externalLink?.isNotEmpty()!!) {
                view.tv_mall_store_title.text = context.getString(
                    R.string.title_map_escalator_to,
                    nodeModel.node.externalLink!![0].toName
                )
            }

            view.setOnClickListener {
                RxBus.publish(CloseMinifiedStoreCardEvent())

            }

        }

        view.tv_mall_store_open_close_statu.text = nodeModel.floor?.floorName


        invalidate()
    }

    fun showStoreNavigationCard(location: NodeModel, totalTime: Int) {
        isCardOpen = false
        this.totalTime = totalTime
        Glide.with(context).load(location.locations?.location_details?.logo_url)
            .into(view.iv_store_item_image)

        view.tv_mall_store_open_close_statu.text = location.locations?.location_details?.name
        view.tv_mall_store_title.text = totalTime.toString()
        tv_min_label.visible()
        iv_mall_store_go_arrow.setImageResource(R.drawable.ic_close_circle)

        iv_mall_store_go_arrow.setOnClickListener {
            RxBus.publish(CloseMinifiedStoreCardEvent())
        }

        iv_open_close_statu.setImageResource(R.drawable.ic_oval_arrow_up)
        view.setOnClickListener {
            //            if (isCardOpen) {
//                RxBus.publish(CloseNavigationCardEvent())
//            } else {
            RxBus.publish(OpenNavigationCardEvent(location))
        }

        invalidate()


    }


    fun toggelCardOpenStatu() {
        this.isCardOpen = !isCardOpen
        iv_open_close_statu.setImageResource(if (isCardOpen) R.drawable.ic_oval_arrow_down else R.drawable.ic_oval_arrow_up)
    }
}
