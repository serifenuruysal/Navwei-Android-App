package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.*
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseRootFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.StoreFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.FloorMapAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.MapStoreListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.StoreAdapterType
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.*
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.view.MapManager
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.*
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.map_fragment.*
import javax.inject.Inject


/**
 * Created by S.Nur Uysal on 2019-10-29.
 */

class MapFragment : BaseRootFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)
    }

    private lateinit var mapManager: MapManager

    private lateinit var subscribeOnClickLocationSelectedEvent: Disposable
    private lateinit var subscribeOnClickFloorMapSelectedEvent: Disposable
    private lateinit var subscribeOnClickStoreNodeEvent: Disposable
    private lateinit var subscribeOnClickStoreCloseEvent: Disposable

    private lateinit var subscribeOpenStoreCardEvent: Disposable
    private lateinit var subscribeCloseStoreCardEvent: Disposable
    private lateinit var subscribeOpenNavigationCardEvent: Disposable
    private lateinit var subscribeCloseNavigationCardEvent: Disposable
    private lateinit var subscribeCloseMinifiedStoreCardEvent: Disposable
    private lateinit var subscribeOpenMinifiedStoreCardEvent: Disposable
    private lateinit var subscribeOpenStorePageEvent: Disposable
    private lateinit var subscribeSetSourceFocusEvent: Disposable
    private lateinit var subscribeMapLoadedEvent: Disposable

    private var selectedMall: Locations? = null
    private var selectedStoreCard: Locations? = null
    private var selectedDestination: Locations? = null

    private lateinit var sourceLocationAdapter: MapStoreListAdapter
    private lateinit var destinationLocationAdapter: MapStoreListAdapter

    private var pointNodeModelList: MutableList<NodeModel>? = mutableListOf()

    private var isAutoFocusAfterSelection = true
    private var isMenuOpened = false
    private var isLoading = false

    private lateinit var selectedFloor: Floor
    private var isMapActionsEnable = true

    private var floorMapAdapter: FloorMapAdapter? = null


    private val stateObserver = Observer<MapPageState> { state ->
        state?.let {
            when (state) {
                is MapDataLoadedState -> {
                    isLoading = false

                    if (state.floorList?.isNotEmpty()!!) {

                        initializeFloorsRecyclerView(ArrayList(state.floorList!!))
                        selectedFloor = state.floorList!![0]
                    }


                    if (state.nodeModelList != null) {


                        state.nodeModelList?.forEach {
                            if (it.node.type == NodeType.point && it.locations != null)
                                this.pointNodeModelList?.add(it)
                        }


                        fillMapData(state.floorMapMap, state.nodeModelList!!)

                        fillSourceList(this.pointNodeModelList!!)
                        fillDestinationList(this.pointNodeModelList!!)


                    } else {
                        showNoMapMessage()
                    }

                }
                is VouchersUpdateState -> {
                    storeCardView.setStoreVoucherData(state.voucherList)
                }
                is LoadingState -> {
                    isLoading = true

                }
                is MapErrorState -> {
                    isLoading = false
                    showNoMapMessage()
                }
                is ErrorState -> {
                    isLoading = false

                }
            }
        }
    }

    private fun showNoMapMessage() {
        view_loading.gone()
        tv_title_empty_list.visible()

    }

    companion object {
        fun newInstance(
            selectedMall: Locations?,
            navigateToMapLocation: Locations?,
            selectedStoreCard: Locations?
        ): MapFragment {
            val f = MapFragment()

            val args = Bundle()
            args.putParcelable("selectedMall", selectedMall)
            args.putParcelable("selectedDestination", navigateToMapLocation)
            args.putParcelable("selectedStoreCard", selectedStoreCard)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedMall = arguments?.getParcelable("selectedMall")
        selectedDestination = arguments?.getParcelable("selectedDestination")
        selectedStoreCard = arguments?.getParcelable("selectedStoreCard")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_backround.gone()
        mapManager = MapManager(
            map_view,
            context!!
        )
        observeViewModel()
        initListeners()
        subscribe()
        setToolbarTitle(selectedMall?.location_details?.name)
        viewModel.getFloors(selectedMall?.id!!)

    }


    private fun initListeners() {
        iv_escalator_button.setOnClickListener {
            selectedFloor = mapManager.getNextFloorToDestination()
            setSelectedFloor()
        }

        cv_lost_button.setOnClickListener {
            information_box_view.visible()
            information_box_view.showAreYouLostContent()

        }

        et_destination_store_label.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setDestinationStoreFocused()
            }
        }

        et_source_store_label.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setSourceStoreFocused()
            }
        }

        et_destination_selected_label.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setDestinationSelectedFocused()
            }
        }

        et_source_selected_label.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setSourceSelectedFocused()
            }
        }


        et_destination_store_label.afterTextChanged {
            searchAtDestinationList(it)
        }

        et_source_store_label.afterTextChanged {
            searchAtSourceList(it)
        }

        cv_menu_map.setOnClickListener {
            if (isMenuOpened) {
                cv_menu_map_peopleup.visibility = View.GONE
                cv_menu_map_flag.visibility = View.GONE
            } else {
                cv_menu_map_peopleup.visibility = View.VISIBLE
                cv_menu_map_flag.visibility = View.VISIBLE
            }
            isMenuOpened = !isMenuOpened

        }

        btn_show_path.setOnClickListener {

            isMapActionsEnable = false
            mapManager.isGoButtonClicked = true
            showPathOnMap()
            setSelectedFloor()
            cv_destination_store_label.gone()
            cv_source_store_label.gone()
            cv_source_and_destination.gone()
            cv_lost_button.visible()
            btn_show_path.gone()
            minifedStoreCard.showStoreNavigationCard(
                mapManager.destinationLocation!!,
                mapManager.getTotalTime()
            )
            minifedStoreCard.visible()


        }

        iv_close_source_destination_close_button.setOnClickListener {
            initSourceAndDestination()
            closeNavigationCard()
            closeMinifiedStoreCard()
        }

    }

    private fun setSelectedStoreCard() {
        val nodeModel = viewModel.getNodeModelOfLocation(selectedStoreCard)
        if (nodeModel != null) {
            selectedFloor = nodeModel.floor!!
            setSelectedFloor()
            openMinifiedStoreCard(nodeModel)

            mapManager.zoomToSelectedStore(nodeModel)

        }

    }

    private fun setSelectedDestination() {
        setDestinationSelected(viewModel.getNodeModelOfLocation(selectedDestination))
    }

    private fun setSelectedFloor() {
        mapManager.setSelectedFloor(selectedFloor)
        floorMapAdapter?.setSelected(selectedFloor)

        if (mapManager.isShowEscalatorButton()) {
            val resourceEscalator = mapManager.getShowEscalatorButtonResource()
            iv_escalator_button.setImageResource(resourceEscalator)
            iv_escalator_button.visible()
        } else {
            iv_escalator_button.gone()
        }
    }

    private fun subscribe() {

        subscribeSetSourceFocusEvent =
            RxBus.listen(SetSourceFocusEvent::class.java).subscribe {
                isMapActionsEnable = true
                clearPathOnMap()
                closeMinifiedStoreCard()
                iv_escalator_button.gone()
                cv_lost_button.gone()
                setSourceSelectedFocused()
            }


        subscribeOnClickStoreNodeEvent =
            RxBus.listen(OnClickStoreNodeEvent::class.java).subscribe { event ->
                if (isMapActionsEnable) {
                    closeMinifiedStoreCard()
                    openMinifiedStoreCard(event.nodeModel)
                }
            }

        subscribeOnClickLocationSelectedEvent =
            RxBus.listen(OnClickLocationSelectedEvent::class.java).subscribe { event ->
                if (isMapActionsEnable) {
                    closeStoreCard()
                    if (event.storeAdapterType == StoreAdapterType.SOURCE) {

                        setSourceSelected(event.locations)

                    } else {

                        setDestinationSelected(event.locations)
                    }
                }
            }

        subscribeOnClickFloorMapSelectedEvent =
            RxBus.listen(OnClickFloorMapSelectedEvent::class.java).subscribe { event ->
                selectedFloor = event.floor
                setSelectedFloor()
            }

        subscribeOnClickStoreCloseEvent =
            RxBus.listen(OnClickStoreCloseEvent::class.java).subscribe {
                if (isMapActionsEnable) {
                    closeMinifiedStoreCard()
                    initSourceAndDestination()
                    lostFocus(et_destination_store_label)
                    lostFocus(et_source_store_label)
                }

            }

        subscribeOpenStoreCardEvent =
            RxBus.listen(OpenStoreCardEvent::class.java).subscribe {
                openStoreCard(it.nodeModel)
            }

        subscribeCloseStoreCardEvent =
            RxBus.listen(CloseStoreCardEvent::class.java).subscribe {
                closeStoreCard()
            }

        subscribeOpenNavigationCardEvent =
            RxBus.listen(OpenNavigationCardEvent::class.java).subscribe {
                openNavigationCard()
            }

        subscribeCloseNavigationCardEvent =
            RxBus.listen(CloseNavigationCardEvent::class.java).subscribe {
                closeNavigationCard()
            }

        subscribeCloseMinifiedStoreCardEvent =
            RxBus.listen(CloseMinifiedStoreCardEvent::class.java).subscribe {
                closeMinifiedStoreCard()
                initSourceAndDestination()
                lostFocus(et_destination_store_label)
                lostFocus(et_source_store_label)
            }

        subscribeOpenMinifiedStoreCardEvent =
            RxBus.listen(OpenMinifiedStoreCardEvent::class.java).subscribe {
                closeStoreCard()
            }

        subscribeOpenStorePageEvent =
            RxBus.listen(OpenStorePageEvent::class.java).subscribe { event ->
                activity?.let {
                    val activity: AppCompatActivity = activity as AppCompatActivity

                    if (activity.getCurrentFragment() !is StoreFragment) {
                        activity.replaceFragment(
                            R.id.frame_main_content,
                            StoreFragment.newInstance(event.location, selectedMall), true
                        )
                    }
                }
            }

        subscribeMapLoadedEvent =
            RxBus.listen(MapLoadedEvent::class.java).subscribe { event ->
                view_loading.gone()
                initSourceAndDestination()
                cv_floor_list.visible()
                if (selectedStoreCard != null) {
                    setSelectedStoreCard()
                } else if (selectedDestination != null) {
                    setSelectedDestination()
                }
                subscribeMapLoadedEvent.dispose()
            }

    }

    private fun initSourceAndDestination() {
        mapManager.sourceLocation = null
        mapManager.destinationLocation = null
        isAutoFocusAfterSelection = true
        isMapActionsEnable = true

        clearPathOnMap()

        fillSourceList(pointNodeModelList!!)
        fillDestinationList(pointNodeModelList!!)

        lostFocus(et_source_store_label)
        lostFocus(et_destination_selected_label)

        et_destination_store_label.text.clear()
        et_source_store_label.text.clear()
        et_destination_store_label.hint = (getString(R.string.title_where_to))

        rv_destination_stores_list.gone()
        rv_source_stores_list.gone()
        v_destination_store_line.gone()

        cv_source_and_destination.gone()
        rv_filter_floors_list.visible()


        btn_show_path.gone()
//        cv_menu_map.visible()
        storeCardView.gone()
        cv_lost_button.gone()
        iv_escalator_button.gone()
        information_box_view.gone()

        cv_source_store_label.gone()
        cv_destination_store_label.visible()
        rv_destination_stores_list.gone()
    }

    private fun setSourceSelectedFocused() {
        mapManager.sourceLocation = null
        fillSourceList(pointNodeModelList!!)

        et_source_store_label.text.clear()
        et_source_store_label.hint = getString(R.string.title_start_point_hint)

        cv_source_store_label.visible()
        rv_source_stores_list.visible()

        v_source_store_line.visible()
        cv_destination_store_label.visible()
        cv_source_and_destination.gone()
        rv_filter_floors_list.gone()
//        cv_menu_map.visible()
        btn_show_path.gone()
    }

    private fun setDestinationSelectedFocused() {
        mapManager.destinationLocation = null
        fillDestinationList(pointNodeModelList!!)

        et_destination_store_label.text.clear()
        et_destination_store_label.hint = getString(R.string.title_start_point_hint)

        cv_source_store_label.visible()
        cv_destination_store_label.visible()
        rv_destination_stores_list.visible()

        v_destination_store_line.visible()
        cv_source_and_destination.gone()
//        cv_menu_map.visible()
        btn_show_path.gone()
        rv_filter_floors_list.gone()
        isAutoFocusAfterSelection = false
    }

    private fun setDestinationStoreFocused() {
        mapManager.destinationLocation = null
        et_destination_store_label.text.clear()
        et_destination_store_label.hint = getString(R.string.title_find_destination)

        fillDestinationList(pointNodeModelList!!)

        rv_destination_stores_list.visible()

        v_destination_store_line.visible()
        cv_destination_store_label.visible()

        rv_filter_floors_list.gone()

        cv_source_and_destination.gone()

        if (mapManager.sourceLocation == null) {
            rv_source_stores_list.gone()
            v_source_store_line.gone()
        }
    }

    private fun setSourceStoreFocused() {
        mapManager.sourceLocation = null
        cv_source_and_destination.gone()
        rv_destination_stores_list.gone()

        v_destination_store_line.gone()

        et_source_store_label.text.clear()
        et_source_store_label.hint = getString(R.string.title_start_point_hint)
        fillSourceList(pointNodeModelList!!)

        cv_source_store_label.visible()
        rv_source_stores_list.visible()

        v_source_store_line.visible()
        rv_filter_floors_list.gone()

    }

    private fun setSourceSelected(location: NodeModel) {
        mapManager.sourceLocation = location
        storeCardView.gone()

        rv_source_stores_list.gone()

        v_source_store_line.gone()

        val str = getSpannableSourceLabel(location)

        et_source_selected_label.setText(str)
        et_source_store_label.setText(str)
        et_source_store_label.clearFocus()
        et_destination_selected_label.clearFocus()

        closeMinifiedStoreCard()
        lostFocus(et_source_store_label)
        lostFocus(et_destination_selected_label)

        cv_menu_map.gone()

        rv_filter_floors_list.visible()

        showPathOnMap()

    }


    private fun setDestinationSelected(location: NodeModel?) {
        if (location == null)
            return

        mapManager.destinationLocation = location

        val str = getSpannableDestinationLabel(location)

        et_destination_selected_label.setText(str)
        et_destination_store_label.setText(str)
        et_destination_store_label.clearFocus()
        et_destination_selected_label.clearFocus()

        rv_destination_stores_list.gone()

        v_destination_store_line.gone()
        cv_destination_store_label.visible()

        if (isAutoFocusAfterSelection)
            setSourceStoreFocused()

        showPathOnMap()
    }


    private fun getSpannableSourceLabel(location: NodeModel): SpannableString {
        val boldText = "${mapManager.sourceLocation?.locations?.location_details?.name!!}  "
        val normalText = location.floor?.floorName
        val str = SpannableString(boldText + normalText)
        str.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            boldText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val i = "$boldText$normalText".indexOf(normalText!!)
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_text_color))
        str.setSpan(colorSpan, i, i + normalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return str
    }

    private fun getSpannableDestinationLabel(location: NodeModel): SpannableString {
        val boldText = "${mapManager.destinationLocation?.locations?.location_details?.name!!}  "
        val normalText = location.floor?.floorName
        val str = SpannableString(boldText + normalText)
        str.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            boldText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val i = "$boldText$normalText".indexOf(normalText!!)
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_text_color))
        str.setSpan(colorSpan, i, i + normalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return str
    }

    private fun setSourceListMaxHeight() {
        if (sourceLocationAdapter.count > 0) {
            val item = sourceLocationAdapter.getView(0, null, rv_source_stores_list)
            item.measure(0, 0)
            val params = rv_source_stores_list.layoutParams
            var count = 4
            if (sourceLocationAdapter.count < 4) {
                count = sourceLocationAdapter.count
            }
            params.height = (count * dpToPx(53f))
            rv_source_stores_list.layoutParams = params
            rv_source_stores_list.requestLayout()
            cl_source_store_label.requestLayout()
            rv_source_stores_list.visible()
            v_source_store_line.visible()
        } else {
            rv_source_stores_list.gone()
            v_source_store_line.gone()
        }
    }

    private fun setDestinationListMaxHeight() {
        if (destinationLocationAdapter.count > 0) {
            val item = destinationLocationAdapter.getView(0, null, rv_destination_stores_list)
            item.measure(0, 0)
            val params = rv_destination_stores_list.layoutParams
            var count = 4
            if (destinationLocationAdapter.count < 4) {
                count = destinationLocationAdapter.count
            }
            params.height = (count * dpToPx(53f))
            rv_destination_stores_list.layoutParams = params
            rv_destination_stores_list.requestLayout()
            cl_destination_store_label.requestLayout()

            rv_destination_stores_list.visible()
            v_destination_store_line.visible()
        } else {
            rv_destination_stores_list.gone()
            v_destination_store_line.gone()
        }
    }


    private fun openMinifiedStoreCard(model: NodeModel) {

        model.locations?.let { viewModel.getAllVouchersByLocation(it) }
        minifedStoreCard.showStoreInfoCard(model)
        minifedStoreCard.visible()

    }

    private fun closeMinifiedStoreCard() {

        view_backround.gone()
        minifedStoreCard.gone()
        storeCardView.gone()

    }

    private fun openStoreCard(locations: NodeModel) {
        closeStoreCard()
        storeCardView.setStoreData(locations)
        view_backround.visible()
        storeCardView.gone()

        slideUp(storeCardView)
        storeCardView.visible()

    }

    private fun slideDown(view: View) {
        val height = view.height
        ObjectAnimator.ofFloat(view, "translationY", 0.toFloat(), height.toFloat()).apply {
            duration = 500
            start()
        }
    }

    private fun slideUp(view: View) {
        val height = view.height
        ObjectAnimator.ofFloat(view, "translationY", height.toFloat(), 0.toFloat()).apply {
            duration = 500
            start()
        }
    }


    private fun closeStoreCard() {
        view_backround.gone()
        slideDown(storeCardView)
        storeCardView.gone()
        viewModel.clearVoucherList()

    }

    private fun openNavigationCard() {
        isMapActionsEnable = false

        val activity: AppCompatActivity = context as AppCompatActivity
        activity.addFragment(
            R.id.frame_main_content,
            MapPathStepsFragment.newInstance(
                mapManager.sourceLocation, mapManager.destinationLocation, selectedMall,
                mapManager.getPathStepsList(),
                selectedFloor,
                mapManager.getTotalTime().toString()
            ), true
        )

    }

    private fun closeNavigationCard() {

        minifedStoreCard.gone()
        initSourceAndDestination()

    }

    private fun showPathOnMap() {
        if (mapManager.sourceLocation == null || mapManager.destinationLocation == null) {
            clearPathOnMap()
            return
        }
        lostFocus(et_destination_store_label)
        lostFocus(et_source_store_label)

        cv_destination_store_label.gone()
        cv_source_store_label.gone()
        cv_source_and_destination.visible()

        val newSelectedFloor =
            mapManager.showPathOnMap()

        rv_filter_floors_list.visible()

        if (newSelectedFloor != null) {
            if (this.selectedFloor.floorId != newSelectedFloor.floorId) {
                selectedFloor = newSelectedFloor
            }


            mapManager.setSelectedFloor(selectedFloor)
            floorMapAdapter?.setSelected(selectedFloor)

            val pathNodesByFloorList = mapManager.getPathNodesByFloorList()
            val enableFloorList = mutableListOf<Floor>()

            pathNodesByFloorList.forEach {
                enableFloorList.add(it)
            }
            if (enableFloorList.isEmpty()) {
                enableFloorList.addAll(viewModel.getFloorList())
            }
            floorMapAdapter?.setEnableList(ArrayList(enableFloorList))

            closeMinifiedStoreCard()
            btn_show_path.visible()
            cv_menu_map.gone()


        } else {
//            Log.d("MapFragment", "The Path should not drawn at map.")
            Toast.makeText(context, "The Path should not drawn at map.", Toast.LENGTH_LONG)
//            throw Exception("The Path should not drawn at map.")
        }
    }


    private fun clearPathOnMap() {
        floorMapAdapter?.setEnableList(viewModel.getFloorList())
        mapManager.clearPathOnMap()
    }

    private fun fillDestinationList(list: List<NodeModel>) {
        destinationLocationAdapter = MapStoreListAdapter(
            context!!,
            list,
            StoreAdapterType.DESTINATION
        )
        rv_destination_stores_list.adapter = destinationLocationAdapter
        setDestinationListMaxHeight()
//        rv_destination_stores_list.invalidate()

    }

    private fun fillSourceList(list: List<NodeModel>) {

        sourceLocationAdapter = MapStoreListAdapter(
            context!!,
            list,
            StoreAdapterType.SOURCE
        )
        rv_source_stores_list.adapter = sourceLocationAdapter
        setSourceListMaxHeight()
//        rv_source_stores_list.invalidate()


    }


    private fun fillMapData(floorMapMap: HashMap<Floor, FloorMap>, storeList: List<NodeModel>) {
        mapManager.initMapData(floorMapMap, storeList, selectedFloor)

    }


    private fun initializeFloorsRecyclerView(floorList: ArrayList<Floor>) {

        rv_filter_floors_list.apply {
            floorMapAdapter = FloorMapAdapter(floorList)
            rv_filter_floors_list.adapter = floorMapAdapter
        }

    }

    private fun searchAtDestinationList(text: String) {
        fillDestinationList(getFilteredList(text))

    }

    private fun searchAtSourceList(text: String) {
        fillSourceList(getFilteredList(text))

    }

    private fun lostFocus(v: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun getFilteredList(text: String?): MutableList<NodeModel> {
        val list: MutableList<NodeModel> = mutableListOf()

        if (viewModel.getNodeModeList()!!.isNotEmpty()) {
            viewModel.getNodeModeList()?.forEach {
                if (it.locations != null) {
                    if (text.isNullOrEmpty() || "" == text) {
                        list.add(it)
                    } else if (it.locations?.location_details?.name?.contains(text!!, true)!!) {
                        list.add(it)
                    }
                }
            }
        }
        return list
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.stateLiveData.removeObserver(stateObserver)

        if (!subscribeOnClickLocationSelectedEvent.isDisposed) {
            subscribeOnClickLocationSelectedEvent.dispose()
        }
        if (!subscribeOnClickFloorMapSelectedEvent.isDisposed) {
            subscribeOnClickFloorMapSelectedEvent.dispose()
        }
        if (!subscribeOnClickStoreNodeEvent.isDisposed) {
            subscribeOnClickStoreNodeEvent.dispose()
        }
        if (!subscribeOnClickStoreCloseEvent.isDisposed) {
            subscribeOnClickStoreCloseEvent.dispose()
        }
        if (!subscribeOpenStoreCardEvent.isDisposed) {
            subscribeOpenStoreCardEvent.dispose()
        }
        if (!subscribeCloseStoreCardEvent.isDisposed) {
            subscribeCloseStoreCardEvent.dispose()
        }
        if (!subscribeOpenNavigationCardEvent.isDisposed) {
            subscribeOpenNavigationCardEvent.dispose()
        }
        if (!subscribeCloseNavigationCardEvent.isDisposed) {
            subscribeCloseNavigationCardEvent.dispose()
        }
        if (!subscribeCloseMinifiedStoreCardEvent.isDisposed) {
            subscribeCloseMinifiedStoreCardEvent.dispose()
        }
        if (!subscribeOpenMinifiedStoreCardEvent.isDisposed) {
            subscribeOpenMinifiedStoreCardEvent.dispose()
        }
        if (!subscribeOpenStorePageEvent.isDisposed) {
            subscribeOpenStorePageEvent.dispose()
        }
        if (!subscribeSetSourceFocusEvent.isDisposed) {
            subscribeSetSourceFocusEvent.dispose()
        }
        if (!subscribeMapLoadedEvent.isDisposed) {
            subscribeMapLoadedEvent.dispose()
        }

    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

}


