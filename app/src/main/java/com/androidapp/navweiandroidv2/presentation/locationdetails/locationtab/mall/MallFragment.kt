package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.mall

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.*
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseRootFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.CategoryClickEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.CategoryListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.StoreListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickStoreEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickStoreToMapEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.StoreFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.storelist.StoreListFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.MapFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.SlideViewPagerAdapter
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.getCurrentFragment
import com.androidapp.navweiandroidv2.util.ext.replaceFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.mall_fragment.*
import kotlinx.android.synthetic.main.mall_fragment.cl_preLoading
import kotlinx.android.synthetic.main.mall_fragment.rv_store_offers
import kotlinx.android.synthetic.main.mall_fragment.toolbar
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
class MallFragment : BaseRootFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MallViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MallViewModel::class.java)
    }

    private var isLoading = false
    private var isLastPage = false

    private var selectedMall: Locations? = null

    private lateinit var subscribeOnClickStoreEvent: Disposable
    private lateinit var subscribeCategoryClickEvent: Disposable
    private lateinit var subscribeOnClickStoreToMapEvent: Disposable

    private val stateObserver = Observer<MallPageState> { state ->
        state?.let {
            isLastPage = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false

                    if (state.categoryList.isNotEmpty()){
                        initializeStoreCategoryRecyclerView(state.categoryList)
                    }

                    if (state.sliderList.isNotEmpty()){
                        initSliderPage(state.sliderList)
                    }

                    if (state.filteredLocationList.isNotEmpty()) {
                        if(state.filteredLocationList.size>3){
                            updateStoreRecyclerView(getFirstThree(state.filteredLocationList))
                            tv_show_more_stores.visibility = View.VISIBLE
                        } else {
                            updateStoreRecyclerView(state.filteredLocationList)
                            tv_show_more_stores.visibility = View.GONE
                        }
                        tv_trending.visibility = View.VISIBLE
                    } else {
                        tv_show_more_stores.visibility = View.GONE
                        tv_trending.visibility = View.GONE
                        line_divider.visibility = View.GONE
                    }

                    cl_preLoading.visibility = View.GONE
                }
                is LoadingState -> {
                    tv_trending.visibility = View.VISIBLE
                    isLoading = true
                }
                is ErrorState -> {
                    isLoading = false
                    tv_trending.visibility = View.GONE
                    cl_preLoading.visibility = View.GONE
                    tv_show_more_stores.visibility = View.GONE
                    line_divider.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        fun newInstance(
            selectedMall: Locations?
        ): MallFragment {
            val f = MallFragment()

            val args = Bundle()
            args.putParcelable("selectedMall", selectedMall)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("serife","mall view created")

        selectedMall = arguments?.getParcelable("selectedMall")
    }

    override fun onResume() {
        super.onResume()
        subscribe()
        observeViewModel()
        getPageData()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)

        if (!subscribeOnClickStoreEvent.isDisposed) {
            subscribeOnClickStoreEvent.dispose()
        }

        if (!subscribeCategoryClickEvent.isDisposed) {
            subscribeCategoryClickEvent.dispose()
        }

        if (!subscribeOnClickStoreToMapEvent.isDisposed) {
            subscribeOnClickStoreToMapEvent.dispose()
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
        return inflater.inflate(R.layout.mall_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cl_preLoading.visibility = View.VISIBLE
        subscribe()
        observeViewModel()
        getPageData()
        initListener()

        setToolbarMenu(R.menu.menu_search)
        setToolbarTitle(selectedMall?.location_details?.name)
        toolbar.setOnMenuItemClickListener(this)
    }

    private fun getPageData() {
        selectedMall?.id?.let {
            viewModel.getCategories()
            viewModel.getStoresByLocationId(it)
            viewModel.getSliders(it)
        }
    }

    private fun initListener() {
        tv_show_more_stores.setOnClickListener {
            goToStoreListPage(null)
        }
    }

    private fun initSliderPage(data: List<Slider>) {
        val itemList: MutableList<SliderItem> = mutableListOf()

        data.forEach { slider ->
            slider.items.forEach { item ->
                itemList.add(item)
            }
        }

        vp_mall_fragment_slide_view.adapter = SlideViewPagerAdapter(context!!, itemList)
        dots_indicator_mall_fragment.setViewPager(vp_mall_fragment_slide_view)
    }

    private fun updateStoreRecyclerView(data: List<Locations>) {
        rv_store_offers.apply {
            rv_store_offers.adapter = StoreListAdapter(data)
        }
    }

    private fun initializeStoreCategoryRecyclerView(data: ArrayList<Category>) {
        rv_mall_store_categories.adapter = CategoryListAdapter(data)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.miSearch -> {
                goToStoreListPage(null)
            }
        }

        return false
    }

    private fun subscribe() {
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

        subscribeCategoryClickEvent =
            RxBus.listen(CategoryClickEvent::class.java).subscribe {
                val selectedCategoryList: MutableList<Category> = mutableListOf()
                selectedCategoryList.add(it.lastSelectedCategory)
                val selectedFilters = SelectedFilters(null, selectedCategoryList, false, null)
                goToStoreListPage(selectedFilters)
                subscribeCategoryClickEvent.dispose()
            }
    }

    private fun goToStoreListPage(selectedFilters: SelectedFilters?) {
        context?.let {
            val activity: AppCompatActivity = it as AppCompatActivity

            activity.replaceFragment(
                R.id.frame_main_content,
                StoreListFragment.newInstance(
                    selectedMall,
                    selectedFilters
                ), true
            )
        }
    }

    private fun getFirstThree(data: List<Locations>): MutableList<Locations> {
        val dataList: MutableList<Locations> = mutableListOf()

        if (data.isNotEmpty())
            dataList.add(data[0])
        if (data.size > 1)
            dataList.add(data[1])
        if (data.size > 2)
            dataList.add(data[2])

        return dataList
    }

}
