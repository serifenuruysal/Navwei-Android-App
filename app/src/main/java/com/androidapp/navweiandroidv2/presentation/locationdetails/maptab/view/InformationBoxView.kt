package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.SetSourceFocusEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.gone
import com.androidapp.navweiandroidv2.util.ext.visible
import kotlinx.android.synthetic.main.view_information_box.view.*


/**
 * Created by S.Nur Uysal on 2019-12-25.
 */

class InformationBoxView : RelativeLayout {


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

    private fun initListeners() {
        view.iv_check_button_information_box.setOnClickListener {
            view.gone()
            RxBus.publish(
                SetSourceFocusEvent()
            )
        }

        view.iv_close_button_information_box.setOnClickListener {

            view.gone()
        }

    }

    private fun initView() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.view_information_box, this, true)

        initListeners()
    }

    fun showAreYouLostContent() {

        view.visible()
//        view.tv_content_information_box.text = context.getString(R.string.title_are_you_lost)


    }


}