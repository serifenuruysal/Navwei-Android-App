package com.androidapp.navweiandroidv2.presentation.parentlocation.filters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.*
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseFragment
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.CitySelectedEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.CountrySelectedEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.filters.adapter.TypeFilterListAdapter
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home_filter.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-11-05.
 */
class HomeFiltersFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: HomeFiltersViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(HomeFiltersViewModel::class.java)
    }

    private var selectedOption: SelectedOption? = null
    private var typeList: ArrayList<Type> = arrayListOf()
    private var mallList: ArrayList<Locations> = arrayListOf()
    private val usedCountriesList = arrayListOf<String>()
    private val usedCitiesList = arrayListOf<City>()

    private val stateObserver = Observer<ChooseStoreFiltersState> { state ->
        state?.let {
            when (state) {
                is DefaultState -> {
                    fillCountryCombo(state.countryList)
                    fillCityCombo(state.selectedCountryName)
                }
                is UpdateState -> {
                    fillCityCombo(state.selectedCountryName)
                }
            }
        }
    }

    companion object {
        fun newInstance(
            selectedOption: SelectedOption?,
            typeList: ArrayList<Type>,
            mallList: ArrayList<Locations>
        ): HomeFiltersFragment {
            val f = HomeFiltersFragment()
            val args = Bundle()

            args.putParcelable("selectedOption", selectedOption)
            args.putParcelableArrayList("typeList", typeList)
            args.putParcelableArrayList("mallList", mallList)

            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedOption = arguments?.getParcelable("selectedOption")
        typeList = arguments?.getParcelableArrayList("typeList")!!
        mallList = arguments?.getParcelableArrayList("mallList")!!
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        processData()
        initListeners()
        viewModel.getCountries()
        fillTypeList()

        setToolbarMenu(R.menu.menu_refresh)
        setToolbarTitle(getString(R.string.title_filters))
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    private fun initListeners() {
        tv_apply_filters.setOnClickListener {
            activity?.onBackPressed()
        }

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun processData() {
        mallList.forEach { location ->
            location.country_name?.let { usedCountriesList.add(it) }
            if (!usedCitiesList.any{ it.name == location.city_name }) {
                location.city_name?.let { city_name ->
                    val city = City(null, null, null, location.country_name, null, city_name)
                    usedCitiesList.add(city)
                }
            }
        }
    }

    private fun fillCountryCombo(countryList: List<Country>) {
        val countryNameList = mutableListOf<String>()

        countryNameList.add("All")
        countryList.forEach { country: Country ->
            if (usedCountriesList.contains(country.name)) {
                countryNameList.add(country.name)
            }
        }
        val adapter =
            context?.let {
                ArrayAdapter(it, android.R.layout.simple_spinner_item, countryNameList)
            }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_fragment_country_country.adapter = adapter

        countryList.forEach {
            if (selectedOption?.country?.name == it.name) {
                val index = adapter?.getPosition(it.name)
                sp_fragment_country_country.setSelection(index!!)
            }
        }

        sp_fragment_country_country.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCountryName = parent?.getItemAtPosition(position).toString()
                    countryList.forEach {
                        if (selectedCountryName == it.name) {
                            selectedOption?.country = it
                            RxBus.publish(CountrySelectedEvent(it))
                        }

                    }
                    viewModel.updateCityList(selectedCountryName)
                }
            }
    }

    private fun fillCityCombo(selectCountryName: String?) {
        val cityNameList = mutableListOf<String>()

        cityNameList.add("All")
        if (selectCountryName != "All") {
            usedCitiesList.forEach { city: City ->
                if (city.country_name == selectCountryName) {
                    cityNameList.add(city.name)
                }
            }
        }

        val arrayAdapter =
            context?.let {
                ArrayAdapter(it, android.R.layout.simple_spinner_item, cityNameList)
            }

        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_fragment_country_city!!.adapter = arrayAdapter

        usedCitiesList.forEach {
            if (selectedOption?.city?.name == it.name) {
                val index = arrayAdapter?.getPosition(it.name)
                sp_fragment_country_city.setSelection(index!!)
            }
        }

        sp_fragment_country_city.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    usedCitiesList.forEach {
                        if (selectedItem == it.name) {
                            selectedOption?.city = it
                            RxBus.publish(CitySelectedEvent(it))
                        }
                    }
                }
            }
    }

    private fun fillTypeList() {
        rv_home_filter_categories.apply {
            val adapter = TypeFilterListAdapter(typeList)
            rv_home_filter_categories.adapter = adapter

            if (selectedOption?.type != null) {
                adapter.setSelected(selectedOption?.type!!)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
            }
        }

        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.miRefresh -> {
                // TODO
                // Toast.makeText(context, "Refresh will be implemented", Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return false
    }
}


