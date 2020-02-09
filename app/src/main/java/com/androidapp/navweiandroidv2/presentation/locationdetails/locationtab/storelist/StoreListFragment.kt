package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.storelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.FiltersActivity
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.CategoryFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.StoreListFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.CategoryLogoListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.StoreListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.Constants
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.afterTextChanged
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.mall_fragment.rv_mall_store_categories
import kotlinx.android.synthetic.main.mall_fragment.toolbar
import kotlinx.android.synthetic.main.store_list_fragment.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class StoreListFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: StoreListViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(StoreListViewModel::class.java)
    }

    private var isLoading = false
    private var isLastPage = false

    private var selectedMall: Locations? = null
    private var selectedFilters: SelectedFilters? = null


    lateinit var subscribeCategoryFilterSelectedEvent: Disposable
    lateinit var subscribeStoreListFilterSelectedEvent: Disposable


    private val stateObserver = Observer<MallCategoryPageState> { state ->
        state?.let {
            isLastPage = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false
                    if (state.loadedAllItems) {
                        if (state.categoryList.isNotEmpty()) {
                            selectedFilters = state.selectedFilters
                            initializeStoreCategoryRecyclerView(
                                state.categoryList,
                                state.selectedFilters?.selectedCategoryList!!
                            )
                        }

                        if (state.filteredLocationList.isNotEmpty()) {
                            updateStoreRecyclerView(state.filteredLocationList)
                            tv_title_empty_list.visibility = View.GONE
                            loading_view.visibility = View.GONE
                            rv_store_list.visibility = View.VISIBLE
                        } else {
                            tv_title_empty_list.visibility = View.VISIBLE
                            rv_store_list.visibility = View.GONE
                            progressBar.visibility = View.GONE
                        }

                        if (state.locationList.isNotEmpty()) {
                            et_mall_search_offers.hint = getString(
                                R.string.title_shops_count_hint, state.locationList.size.toString()
                            )
                        }
                    }
                }

                is LoadingState -> {
                    isLoading = true
                }
                is ErrorState -> {
                    isLoading = false
                }
            }
        }
    }

    companion object {
        fun newInstance(
            selectedMall: Locations?,
            selectedFilters: SelectedFilters?
        ): StoreListFragment {
            val f =
                StoreListFragment()

            val args = Bundle()
            args.putParcelable("selectedMall", selectedMall)
            args.putParcelable("selectedFilters", selectedFilters)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedMall = arguments?.getParcelable("selectedMall")
        selectedFilters = arguments?.getParcelable<SelectedFilters>("selectedFilters")
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
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.store_list_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe()
        observeViewModel()
        initListeners()
        viewModel.setSelectedFilters(selectedFilters)
        viewModel.getCategories(selectedMall?.id!!)
        viewModel.getAllVouchers()
        setToolbarMenu(R.menu.menu_filter)

        loading_view.visibility = View.VISIBLE

        setToolbarTitle(getString(R.string.title_stores))
        toolbar.setOnMenuItemClickListener(this)

        tv_title_empty_list.visibility = View.GONE
    }

    private fun initListeners() {
        et_mall_search_offers.afterTextChanged {
            viewModel.searchAtStoreList(it)
        }
    }

    private fun updateStoreRecyclerView(data: List<Locations>) {
        rv_store_list.apply {
            rv_store_list.adapter = StoreListAdapter(data)
        }
    }

    private fun initializeStoreCategoryRecyclerView(
        data: ArrayList<Category>,
        selectedData: MutableList<Category>
    ) {
        rv_mall_store_categories.adapter =
            CategoryLogoListAdapter(
                data,
                selectedData
            )
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.miFilter -> {
                val i = Intent(activity, FiltersActivity::class.java)
                i.putExtra(Constants.SELECTED_FILTER, selectedFilters)
                i.putExtra(Constants.SELECTED_LOCATION, selectedMall)
                startActivity(i)
                return true
            }
        }

        return false
    }

    private fun subscribe() {
        subscribeStoreListFilterSelectedEvent =
            RxBus.listen(StoreListFilterSelectedEvent::class.java).subscribe {
                selectedFilters = it.selectedFilters
                viewModel.setSelectedFilters(selectedFilters)
                viewModel.getLocationsWithSelectedFloor()
            }

        subscribeCategoryFilterSelectedEvent =
            RxBus.listen(CategoryFilterSelectedEvent::class.java).subscribe {
                viewModel.updateCategoryList(it.selectedCategoryList)
            }
    }

    override fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

}