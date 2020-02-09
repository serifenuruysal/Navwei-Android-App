package com.androidapp.navweiandroidv2.presentation.parentlocation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.SelectedOption
import com.androidapp.entity.models.Type
import com.androidapp.navweiandroidv2.BuildConfig
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.parentlocation.adapter.MallListAdapter
import com.androidapp.navweiandroidv2.presentation.parentlocation.adapter.TypeListAdapter
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.CitySelectedEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.ClickFiltersEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.CountrySelectedEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.TypeSelectedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.afterTextChanged
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-25.
 */
class HomeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    private lateinit var subscribeTypeSelectedEvent: Disposable
    private lateinit var subscribeCountrySelectedEvent: Disposable
    private lateinit var subscribeCitySelectedEvent: Disposable

    private var selectedOptions: SelectedOption =
        SelectedOption(
            country = null,
            city = null,
            searchText = null,
            distance = 0,
            type = getDefaultType()
        )

    private var isLoading = false
    private var isLastPage = false

    private lateinit var typeListAdapter: TypeListAdapter

    private val stateObserver = Observer<HomePageState> { state ->
        state?.let {
            isLastPage = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false
//                    shimmer_view_container.stopShimmerAnimation()
//                    shimmer_view_container.visibility = View.GONE
                    if (state.filteredLocationList.isNotEmpty()) {
                        updateMallRecyclerView(state.filteredLocationList)
                        tv_title_empty_list.visibility = View.GONE
                        rv_home_stores.visibility = View.VISIBLE
                        cl_preLoading.visibility = View.GONE
                    } else {
                        tv_title_empty_list.visibility = View.VISIBLE
                        rv_home_stores.visibility = View.GONE
                    }

                    if (state.typeList.isNotEmpty())
                        updateTypeRecyclerView(state.typeList)


                }
                is UpdateState -> {
                    if (state.filteredLocationList.isNotEmpty()) {
                        updateMallRecyclerView(state.filteredLocationList)
                        tv_title_empty_list.visibility = View.GONE
                        rv_home_stores.visibility = View.VISIBLE
                    } else {
                        tv_title_empty_list.visibility = View.VISIBLE
                        rv_home_stores.visibility = View.GONE
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

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_version.text = BuildConfig.VERSION_NAME
        cl_preLoading.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
//        shimmer_view_container.startShimmerAnimation()
        observeViewModel()
        subscribe()
        setListeners()
        viewModel.getMalls()
    }

//    override fun onStop() {
//        super.onStop()
////        shimmer_view_container.stopShimmerAnimation()
//    }

    private fun setListeners() {

        btn_filter.setOnClickListener {
            RxBus.publish(
                ClickFiltersEvent(
                    ArrayList(viewModel.obtainTypeListData()),
                    ArrayList(viewModel.obtainMallListData()),
                    selectedOptions
                )
            )
        }

        et_home_search_mall.afterTextChanged {
            updateMallList()
        }

        et_home_search_mall.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    private fun updateMallRecyclerView(data: List<Locations>) {
        rv_home_stores.apply {
            rv_home_stores.adapter = MallListAdapter(data)
        }
    }

    private fun updateTypeRecyclerView(data: List<Type>) {
        rv_home_store_categories.apply {
            typeListAdapter = TypeListAdapter(data)
            rv_home_store_categories.adapter = typeListAdapter
        }
    }

    private fun subscribe() {
        subscribeTypeSelectedEvent =
            RxBus.listen(TypeSelectedEvent::class.java).subscribe {
                et_home_search_mall?.let { et_home_search_mall.clearFocus() }
                selectedOptions.type = it.type
                typeListAdapter.setSelected(it.type)
                updateMallList()
            }

        subscribeCitySelectedEvent =
            RxBus.listen(CitySelectedEvent::class.java).subscribe {
                et_home_search_mall?.let { et_home_search_mall.clearFocus() }
                selectedOptions.city = it.city
                updateMallList()
            }

        subscribeCountrySelectedEvent =
            RxBus.listen(CountrySelectedEvent::class.java).subscribe {
                et_home_search_mall?.let { et_home_search_mall.clearFocus() }
                selectedOptions.country = it.country
                updateMallList()
            }
    }

    private fun updateMallList() {
        viewModel.filterMallList(
            selectedOptions
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.stateLiveData.removeObserver(stateObserver)
        if (!subscribeTypeSelectedEvent.isDisposed) {
            subscribeTypeSelectedEvent.dispose()
        }
        if (!subscribeCitySelectedEvent.isDisposed) {
            subscribeCitySelectedEvent.dispose()
        }
        if (!subscribeCountrySelectedEvent.isDisposed) {
            subscribeCountrySelectedEvent.dispose()
        }
    }

    private fun getDefaultType(): Type {
        return Type(
            name = "All",
            id = "",
            slug = "",
            picture_url = "",
            icon_url = "",
            active = true
        )
    }

}




