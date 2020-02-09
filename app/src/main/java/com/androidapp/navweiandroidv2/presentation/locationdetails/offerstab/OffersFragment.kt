package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseRootFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.FiltersActivity
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.CategoryFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.StoreListFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.CategoryLogoListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickStoreEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickStoreToMapEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.StoreFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.MapFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapterType
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.events.OnClickOfferEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail.OfferDetailFragment
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.afterTextChanged
import com.androidapp.navweiandroidv2.util.ext.getCurrentFragment
import com.androidapp.navweiandroidv2.util.ext.replaceFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.offers_fragment.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
class OffersFragment : BaseRootFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: OffersViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OffersViewModel::class.java)
    }

    private lateinit var subscribeCategoryFilterSelectedEvent: Disposable
    private lateinit var subscribeStoreListFilterSelectedEvent: Disposable
    private lateinit var subscribeOnClickOfferEvent: Disposable
    private lateinit var subscribeOnClickStoreEvent: Disposable
    private lateinit var subscribeOnClickStoreToMapEvent:Disposable

    private var isLoading = false
    private var selectedMall: Locations? = null
    private var selectedFilters: SelectedFilters? = null
    private val stateObserver = Observer<OffersPageState> { state ->
        state?.let {
            isLoading = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false
                    if (state.categoryList.isNotEmpty())
                        initializeStoreCategoryRecyclerView(
                            state.categoryList,
                            state.selectedFilters?.selectedCategoryList!!
                        )

                    if (state.filteredVoucherList.isNotEmpty()) {
                        rv_store_offers.visibility = View.VISIBLE
                        initializeVouchersRecyclerView(ArrayList(state.filteredVoucherList))
                        tv_title_empty_list.visibility = View.GONE
                        cl_preLoading.visibility = View.GONE
                    } else {
                        tv_title_empty_list.visibility = View.VISIBLE
                        line_divider.visibility = View.GONE
                        rv_store_offers.visibility = View.GONE
                        cl_preLoading.visibility = View.GONE
                    }

                    if (state.voucherList.isNotEmpty()) {
                        et_mall_search_offers.hint = getString(
                            R.string.title_offers_count_hint,
                            state.voucherList.size
                        )
                        et_mall_search_offers.visibility = View.VISIBLE
                    } else {
                        et_mall_search_offers.visibility = View.GONE
                    }
                }
                is LoadingState -> {
                    isLoading = true
                }
                is ErrorState -> {
                    cl_preLoading.visibility = View.GONE
                    tv_title_empty_list.visibility = View.VISIBLE
                    line_divider.visibility = View.GONE
                    isLoading = false
                }
            }
        }
    }

    companion object {
        fun newInstance(selectedMall: Locations?) : OffersFragment {
            val f = OffersFragment()
            val args = Bundle()

            args.putParcelable("selectedMall", selectedMall)
            f.arguments = args

            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.offers_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedMall = arguments?.getParcelable("selectedMall")
        subscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)

        if (!subscribeStoreListFilterSelectedEvent.isDisposed) {
            subscribeStoreListFilterSelectedEvent.dispose()
        }

        if (!subscribeCategoryFilterSelectedEvent.isDisposed) {
            subscribeCategoryFilterSelectedEvent.dispose()
        }

        if (subscribeOnClickOfferEvent.isDisposed) {
            subscribeOnClickOfferEvent.dispose()
        }

        if (subscribeOnClickStoreEvent.isDisposed) {
            subscribeOnClickStoreEvent.dispose()
        }

        if (subscribeOnClickStoreToMapEvent.isDisposed) {
            subscribeOnClickStoreToMapEvent.dispose()
        }
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe()
        observeViewModel()
        initListeners()
        cl_preLoading.visibility = View.VISIBLE
        et_mall_search_offers.visibility = View.GONE

        viewModel.getCategories()
        viewModel.getStoresByLocationId(selectedMall!!)

        setToolbarMenu(R.menu.menu_filter)
        setToolbarTitle(selectedMall?.location_details?.name)

        toolbar.setOnMenuItemClickListener(this)
    }

    private fun initListeners() {
        et_mall_search_offers.afterTextChanged {
            viewModel.searchAtStoreList(it)
        }
    }

    private fun subscribe() {
        subscribeStoreListFilterSelectedEvent =
            RxBus.listen(StoreListFilterSelectedEvent::class.java).subscribe {
                selectedFilters = it.selectedFilters
                viewModel.setSelectedFilters(selectedFilters)
                viewModel.updateCategoryList(selectedFilters?.selectedCategoryList!!)
            }

        subscribeCategoryFilterSelectedEvent =
            RxBus.listen(CategoryFilterSelectedEvent::class.java).subscribe {
                viewModel.updateCategoryList(it.selectedCategoryList)
            }

        subscribeOnClickOfferEvent =
            RxBus.listen(OnClickOfferEvent::class.java).subscribe { event ->
                activity?.let {
                    val activity: AppCompatActivity = activity as AppCompatActivity

                    activity.replaceFragment(
                        R.id.frame_main_content,
                        OfferDetailFragment.newInstance(event.voucher), true
                    )
                    subscribeOnClickOfferEvent.dispose()
                }
            }

        subscribeOnClickStoreEvent =
            RxBus.listen(OnClickStoreEvent::class.java).subscribe { event ->
                activity?.let {
                    val activity: AppCompatActivity = activity as AppCompatActivity

                    if (activity.getCurrentFragment() !is StoreFragment) {
                        activity.replaceFragment(
                            R.id.frame_main_content,
                            StoreFragment.newInstance(event.locations, selectedMall), true
                        )
                    }
                    subscribeOnClickStoreEvent.dispose()
                }
            }

        subscribeOnClickStoreToMapEvent =
            RxBus.listen(OnClickStoreToMapEvent::class.java).subscribe { event ->
                activity?.let {
                    val activity: AppCompatActivity = activity as AppCompatActivity

                    activity.replaceFragment(
                        R.id.frame_main_content,
                        MapFragment.newInstance(selectedMall,event.locations,null), true
                    )
                    subscribeOnClickStoreToMapEvent.dispose()
                }
            }
    }

    private fun initializeStoreCategoryRecyclerView(
        data: ArrayList<Category>,
        selectedData: MutableList<Category>?
    ) {
        rv_mall_store_categories.apply {
            rv_mall_store_categories.adapter =
                CategoryLogoListAdapter(
                    data,
                    selectedData!!
                )
        }
    }

    private fun initializeVouchersRecyclerView(data: ArrayList<Voucher>) {
        rv_store_offers.apply {
            rv_store_offers.adapter = OffersListAdapter(data, true,
                OffersListAdapterType.NORMAL)
        }
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.miFilter -> {
                val i = Intent(activity, FiltersActivity::class.java)

                i.putExtra(Constants.SELECTED_LOCATION, selectedMall)
                i.putExtra(Constants.SELECTED_FILTER, viewModel.getSelectedFilters())
                i.putExtra(Constants.SHOW_OFFER_SWITCH, false)
                startActivity(i)

                return true
            }
        }

        return false
    }

    override fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

}

open class Constants {
    companion object {
        const val SELECTED_FILTER: String = "SELECTED_FILTER"
        const val SELECTED_LOCATION: String = "SELECTED_LOCATION"
        const val SHOW_OFFER_SWITCH: String = "SHOW_OFFER_SWITCH"
    }
}
