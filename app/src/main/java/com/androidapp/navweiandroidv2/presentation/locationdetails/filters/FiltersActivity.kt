package com.androidapp.navweiandroidv2.presentation.locationdetails.filters

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Floor
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedFilters
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseActivity
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.OnClickCategoryEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.OnClickFloorEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.OnClickSwitchStoresWIthOffersEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.StoreListFilterSelectedEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.Constants
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.addFragment
import com.androidapp.navweiandroidv2.util.ext.replaceFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class FiltersActivity : BaseActivity(), HasSupportFragmentInjector {

    private lateinit var subscribeOnClickCategoryEvent: Disposable
    private lateinit var subscribeOnClickFloorEvent: Disposable
    private lateinit var subscribeOnClickSwitchStoresWIthOffersEvent: Disposable

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: FilterViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(FilterViewModel::class.java)
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    private var isLoading = false
    private var floorList: ArrayList<Floor>? = arrayListOf()
    private var categoryList: ArrayList<Category>? = arrayListOf()

    private var selectedFilters: SelectedFilters? = null
    private var selectedMall: Locations? = null
    private var isShowOfferSwitch = true

    private val stateObserver = Observer<FilterPageState> { state ->
        state?.let {
            when (state) {
                is DefaultState -> {
                    isLoading = false
                    if (it.loadedAllItems) {
                        if (state.categoryList.isNotEmpty()) {
                            this.categoryList = ArrayList(state.categoryList)
                        }

                        if (state.floorList.isNotEmpty()) {
                            this.floorList = ArrayList(state.floorList)
                        }
                        if (selectedFilters == null) {
                            val categoryList = mutableListOf<Category>()
                            val category = Category(name = getString(R.string.title_all))
                            if (!categoryList.contains(category))
                                categoryList.add(category)
                            selectedFilters = SelectedFilters(
                                floorList?.get(0),
                                categoryList,
                                false,null
                            )
                        }

                        if (selectedFilters?.selectedCategoryList == null) {
                            selectedFilters?.selectedCategoryList = mutableListOf()
                            val category = Category(name = getString(R.string.title_all))
                            if (!selectedFilters?.selectedCategoryList!!.contains(category))
                                selectedFilters?.selectedCategoryList?.add(category)
                        }

                        if (selectedFilters?.selectedFloor == null) {
                            selectedFilters?.selectedFloor = floorList?.get(0)
                        }
                        fillPageDataWithSelectedFilter()
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

    private fun fillPageDataWithSelectedFilter() {
        addFragment(
            R.id.frame_main_content,
            FilterMainFragment.newInstance(selectedFilters,isShowOfferSwitch),
            true
        )
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        selectedFilters = intent.getParcelableExtra(Constants.SELECTED_FILTER)
        selectedMall = intent.getParcelableExtra(Constants.SELECTED_LOCATION)
        isShowOfferSwitch = intent.getBooleanExtra(Constants.SHOW_OFFER_SWITCH,true)

        subscribe()

        observeViewModel()
        viewModel.getCategories()
        viewModel.getFloors(selectedMall?.id!!)
    }

    private fun subscribe() {
        subscribeOnClickCategoryEvent =
            RxBus.listen(OnClickCategoryEvent::class.java).subscribe {
                replaceFragment(
                    R.id.frame_main_content,
                    FilterAllCategoryFragment.newInstance(
                        ArrayList(selectedFilters?.selectedCategoryList!!), categoryList!!
                    ),
                    true
                )
            }

        subscribeOnClickFloorEvent =
            RxBus.listen(OnClickFloorEvent::class.java).subscribe {
                selectedFilters?.selectedFloor = it.selectedFloor
                replaceFragment(
                    R.id.frame_main_content,
                    FilterFloorFragment.newInstance(
                        selectedFilters?.selectedFloor, floorList!!
                    ), true
                )
            }

        subscribeOnClickSwitchStoresWIthOffersEvent =
            RxBus.listen(OnClickSwitchStoresWIthOffersEvent::class.java).subscribe {
                selectedFilters?.isStoreSwitchOn = it.isTurnedOn
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miRefresh -> {
                // TODO: This should probably do something
            }
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.stateLiveData.removeObserver(stateObserver)
        if (!subscribeOnClickCategoryEvent.isDisposed) {
            subscribeOnClickCategoryEvent.dispose()
        }
        if (!subscribeOnClickFloorEvent.isDisposed) {
            subscribeOnClickFloorEvent.dispose()
        }
        if (!subscribeOnClickSwitchStoresWIthOffersEvent.isDisposed) {
            subscribeOnClickSwitchStoresWIthOffersEvent.dispose()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            RxBus.publish(StoreListFilterSelectedEvent(selectedFilters!!))
            finish()
        }
    }

}
