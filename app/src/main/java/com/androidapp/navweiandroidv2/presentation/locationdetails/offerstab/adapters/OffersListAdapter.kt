package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.events.OnClickOfferEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.formatDate
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_offer.view.*

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */
class OffersListAdapter(
    private val dataList: ArrayList<Voucher>,
    private val isShowLogo: Boolean,
    private val type: OffersListAdapterType
) : RecyclerView.Adapter<OffersListAdapter.VoucherViewHolder>() {

    class VoucherViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VoucherViewHolder {
        if (type.name == OffersListAdapterType.MAP.name) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_offer_map, parent, false)
            return VoucherViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_offer, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = dataList[position]

        Glide.with(holder.view.context).load(voucher.picture_url)
            .into(holder.view.tv_offer_item_image)

        holder.view.tv_offer_item_logo?.let {
            if (isShowLogo) {
                holder.view.tv_offer_item_logo.visibility = View.VISIBLE
                Glide.with(holder.view.context)
                    .load(voucher.cover_url)
                    .into(holder.view.tv_offer_item_logo)
            } else {
                holder.view.tv_offer_item_logo.visibility = View.GONE
            }
        }

        holder.view.tv_offer_item_title.text = voucher.name
        holder.view.tv_offer_item_expire_date.text =
            holder.view.context.getString(
                R.string.title_expire,
                voucher.expired_at?.formatDate()
            )
        holder.view.setOnClickListener {
            RxBus.publish(OnClickOfferEvent(voucher))
        }
    }

    override fun getItemCount() = dataList.size

}

enum class OffersListAdapterType {
    MAP,
    NORMAL
}