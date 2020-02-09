package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidapp.entity.models.Floor
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.MapStepsModel
import com.androidapp.entity.models.NodeModel
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.MapStepsListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.CloseNavigationCardEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OnClickFloorMapSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.gone
import com.androidapp.navweiandroidv2.util.ext.visible
import kotlinx.android.synthetic.main.map_path_steps_fragment.*

/**
 * Created by S.Nur Uysal on 2019-11-14.
 */
class MapPathStepsFragment : BaseFragment() {

    private var sourceLocation: NodeModel? = null
    private var destinationLocation: NodeModel? = null
    private var selectedMall: Locations? = null
    private var pathsStepsMap: HashMap<Floor, MutableList<MapStepsModel>> = hashMapOf()
    private var selectedFloor: Floor? = null
    private var totalTime: String? = null
    private var progressStatu: Int = 1


    companion object {
        fun newInstance(
            sourceLocation: NodeModel?,
            destinationLocation: NodeModel?,
            selectedMall: Locations?,
            pathsStepsMap: HashMap<Floor, MutableList<MapStepsModel>>,
            selectedFloor: Floor,
            totalTime: String
        ): MapPathStepsFragment {
            val f = MapPathStepsFragment()

            val args = Bundle()
            args.putParcelable("destinationLocation", destinationLocation)
            args.putParcelable("sourceLocation", sourceLocation)
            args.putParcelable("selectedMall", selectedMall)
            args.putParcelable("selectedFloor", selectedFloor)
            args.putString("totalTime", totalTime)
            f.arguments = args
            f.pathsStepsMap = pathsStepsMap

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sourceLocation = arguments?.getParcelable("sourceLocation")
        destinationLocation = arguments?.getParcelable("destinationLocation")
        selectedMall = arguments?.getParcelable("selectedMall")
        selectedFloor = arguments?.getParcelable("selectedFloor")
        totalTime = arguments?.getString("totalTime")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_path_steps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }

        setToolbarTitle(selectedMall?.location_details?.name)
        pb_path.max = pathsStepsMap.size

        initListener()
        initData()
        initProgressStatu()

        if (pathsStepsMap.size > 0) {
            updateRecyclerView()
            updateFinishButton()
        }

    }


    private fun initData() {
        tv_source_label.text = sourceLocation?.locations?.location_details?.name
        tv_destination_label.text = destinationLocation?.locations?.location_details?.name

        tv_minute_label.text = totalTime

    }

    private fun initProgressStatu() {


        val diff = Math.abs(destinationLocation?.floor?.weight!! - selectedFloor?.weight!!)
        progressStatu = pathsStepsMap.size - diff

        pb_path.progress = progressStatu
    }

    private fun updateRecyclerView() {
        if (selectedFloor != null && pathsStepsMap.containsKey(selectedFloor!!))
            rv_map_steps.adapter = MapStepsListAdapter(pathsStepsMap[selectedFloor!!]!!)

    }


    private fun initListener() {
        iv_close_button.setOnClickListener {
            finishRoute()
        }

        tv_next_button.setOnClickListener {
            if (progressStatu >= pathsStepsMap.size) {
                finishRoute()
            } else {
                progressStatu++
                pb_path.progress = progressStatu

                if (isGoingUp()) {//going up
                    updateSelectedFloor(selectedFloor?.weight!!.plus(1))

                } else {
                    updateSelectedFloor(selectedFloor?.weight!!.minus(1))
                }
                updateFinishButton()
                updateRecyclerView()
            }
        }

        tv_back_button.setOnClickListener {
            if (isGoingUp()) {//going up
                updateSelectedFloor(selectedFloor?.weight!!.minus(1))

            } else {
                updateSelectedFloor(selectedFloor?.weight!!.plus(1))
            }

            progressStatu -= 1
            pb_path.progress = progressStatu

            updateFinishButton()
            updateRecyclerView()
        }

        iv_close_statu.setOnClickListener {
            activity?.onBackPressed()
        }

    }

    private fun isGoingUp(): Boolean {
        return destinationLocation?.floor?.weight!! > sourceLocation?.floor?.weight!!
    }

    private fun updateFinishButton() {
        if (pathsStepsMap.size == 1) {
            tv_back_button.gone()
            tv_next_button.text = getString(R.string.finish_route)

        } else if (pathsStepsMap.size > 0 && pathsStepsMap.size >= progressStatu) {
            if (selectedFloor?.floorId == destinationLocation?.floor?.floorId) {
                tv_next_button.text = getString(R.string.finish_route)
                if (progressStatu > 1) {
                    tv_back_button.visible()
                } else {
                    tv_back_button.gone()
                }
            } else {
                tv_next_button.text = getString(R.string.next)
                if (progressStatu > 1) {
                    tv_back_button.visible()
                } else {
                    tv_back_button.gone()
                }
            }


        }

    }

    private fun updateSelectedFloor(newWeight: Int) {
        for (me in pathsStepsMap.entries) {
            if (newWeight == me.key.weight) {
                selectedFloor = me.key
                RxBus.publish(OnClickFloorMapSelectedEvent(selectedFloor!!))
                break
            }
        }
    }

    private fun finishRoute() {
        activity?.onBackPressed()
        RxBus.publish(CloseNavigationCardEvent())
    }


}