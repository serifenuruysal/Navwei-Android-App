package com.androidapp.navweiandroidv2.presentation.locationdetails.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.*
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.Constants
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.store_filters_fragment.*

/**
 * Created by S.Nur Uysal on 2019-11-07.
 */

class FilterMainFragment : Fragment() {

    private var selectedFilters: SelectedFilters? = null
    private var isShowOfferSwitch = true
    lateinit var subscribeCategoryFilterSelectedEvent: Disposable
    lateinit var subscribeFloorFilterSelectedEvent: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.store_filters_fragment, container, false)

    }

    companion object {
        fun newInstance(
            selectedFilters: SelectedFilters?,
            isShowOfferSwitch: Boolean
        ): FilterMainFragment {
            val f = FilterMainFragment()

            val args = Bundle()
            args.putParcelable(Constants.SELECTED_FILTER, selectedFilters)
            args.putBoolean(Constants.SHOW_OFFER_SWITCH, isShowOfferSwitch)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        selectedFilters = arguments?.getParcelable(Constants.SELECTED_FILTER)
        isShowOfferSwitch = arguments?.getBoolean(Constants.SHOW_OFFER_SWITCH, true)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setViewsVisibility()
        subscribe()
        updateFilterTitles()
        setToolbar()
    }

    private fun setViewsVisibility() {
        if (!isShowOfferSwitch) {
            tv_content_filter_stores_with_offers.visibility = View.GONE
            sw_content_filter_stores_with_offers.visibility = View.GONE
            v_content_filter_stores_with_offers.visibility = View.GONE
        }
    }

    private fun subscribe() {
        subscribeCategoryFilterSelectedEvent =
            RxBus.listen(CategoryFilterSelectedEvent::class.java).subscribe {
                selectedFilters?.selectedCategoryList = it.selectedCategoryList
                updateFilterTitles()
            }
        subscribeFloorFilterSelectedEvent =
            RxBus.listen(FloorFilterSelectedEvent::class.java).subscribe {
                selectedFilters?.selectedFloor = it.selectedFloor
                updateFilterTitles()
            }
    }

    private fun updateFilterTitles() {
        tv_main_filter_floor_content?.let {
            tv_main_filter_floor_content.text = selectedFilters?.selectedFloor?.floorName
        }

        tv_content_filter_category_content?.let {
            if (!selectedFilters?.selectedCategoryList.isNullOrEmpty()) {
                updateCategoryTitle()
            }
        }

        sw_content_filter_stores_with_offers?.let {
            sw_content_filter_stores_with_offers.isChecked = selectedFilters?.isStoreSwitchOn!!
        }
    }

    private fun updateCategoryTitle(){
        when {
            selectedFilters?.selectedCategoryList?.size!! > 1 -> tv_content_filter_category_content.text =
                getString(R.string.title_multiple_selection)
            selectedFilters?.selectedCategoryList?.size!! == 1 -> tv_content_filter_category_content.text =
                selectedFilters?.selectedCategoryList?.get(0)?.name
            else -> tv_content_filter_category_content.text = getString(R.string.title_all)
        }
    }

    private fun setListeners() {
        tv_content_filter_category_content.setOnClickListener {
            RxBus.publish(OnClickCategoryEvent(selectedFilters?.selectedCategoryList!!))
        }

        tv_main_filter_floor_content.setOnClickListener {
            RxBus.publish(OnClickFloorEvent(selectedFilters?.selectedFloor!!))
        }

        sw_content_filter_stores_with_offers.setOnCheckedChangeListener { _, isChecked ->
            selectedFilters?.isStoreSwitchOn = isChecked
            RxBus.publish(OnClickSwitchStoresWIthOffersEvent(isChecked))

        }

        tv_apply_filters.setOnClickListener {
            // TODO: Should this be deleted?
            // RxBus.publish(StoreListFilterSelectedEvent(selectedFilters!!))
            activity?.onBackPressed()
        }
    }

    private fun setToolbar() {
        tv_title_toolbar.text = getString(R.string.title_filters)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!subscribeCategoryFilterSelectedEvent.isDisposed) {
            subscribeCategoryFilterSelectedEvent.dispose()
        }

        if (!subscribeFloorFilterSelectedEvent.isDisposed) {
            subscribeFloorFilterSelectedEvent.dispose()
        }
    }

}