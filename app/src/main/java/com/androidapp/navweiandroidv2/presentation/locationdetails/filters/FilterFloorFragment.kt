package com.androidapp.navweiandroidv2.presentation.locationdetails.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidapp.entity.models.Floor
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.adapters.FloorListAdapter
import kotlinx.android.synthetic.main.floors_filters_fragment.*
import kotlinx.android.synthetic.main.floors_filters_fragment.toolbar
import kotlinx.android.synthetic.main.floors_filters_fragment.tv_title_toolbar


/**
 * Created by S.Nur Uysal on 2019-11-07.
 */
class FilterFloorFragment : Fragment() {


    private var selectedFloor: Floor? = null
    private var floorList: ArrayList<Floor>? = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.floors_filters_fragment, container, false)

    }

    companion object {
        fun newInstance(
            selectedFloor: Floor?,
            floorList: ArrayList<Floor>
        ): FilterFloorFragment {
            val f = FilterFloorFragment()

            val args = Bundle()
            args.putParcelableArrayList("floorList", floorList)
            args.putParcelable("selectedFloor", selectedFloor)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        selectedFloor = arguments?.getParcelable("selectedFloor")
        floorList = arguments?.getParcelableArrayList("floorList")
        initToolbar()

    }


    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFloorsRecyclerView()
        setToolbar()

    }

    private fun initializeFloorsRecyclerView() {

        rv_filter_floors_list.apply {
            rv_filter_floors_list.adapter = FloorListAdapter(floorList!!, selectedFloor)
        }

    }

    private fun setToolbar() {
        tv_title_toolbar.setText(getString(R.string.title_store_floors))
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }


}