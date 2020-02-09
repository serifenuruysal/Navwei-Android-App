package com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidapp.entity.models.Locations
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseRootFragment
import com.androidapp.navweiandroidv2.util.ext.replaceFragment
import kotlinx.android.synthetic.main.more_fragment.*

/**
 * Created by S.Nur Uysal on 2019-10-29.
 */

class MoreFragment : BaseRootFragment() {
    private var selectedMall: Locations? = null

    companion object {
        fun newInstance(
            selectedMall: Locations?
        ): MoreFragment {
            val f = MoreFragment()

            val args = Bundle()
            args.putParcelable("selectedMall", selectedMall)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedMall = arguments?.getParcelable<Locations>("selectedMall")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.more_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(selectedMall?.location_details?.name)
        setListeners()
    }

    private fun setListeners() {
        cl_how_it_works.setOnClickListener {
            Toast.makeText(context, "How it Works will be implemented", Toast.LENGTH_LONG).show()
        }

        cl_mall_info.setOnClickListener {
            val activity: AppCompatActivity = context as AppCompatActivity
            activity.replaceFragment(
                R.id.frame_main_content,
                MallInfoFragment.newInstance(selectedMall), true
            )
        }

        cl_privacy_and_policy.setOnClickListener {
            Toast.makeText(context, "Privacy and Policy will be implemented", Toast.LENGTH_LONG)
                .show();
        }

        cl_term_and_use.setOnClickListener {
            Toast.makeText(context, "Term and Use will be implemented", Toast.LENGTH_LONG).show()
        }
    }

}