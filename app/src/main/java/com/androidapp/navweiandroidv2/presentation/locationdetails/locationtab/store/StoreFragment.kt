package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.Voucher
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.MapFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapterType
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.events.OnClickOfferEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail.OfferDetailFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerlist.OffersListFragment
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.*
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.store_fragment.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-11-26.
 */

class StoreFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: StoreViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(StoreViewModel::class.java)
    }

    private var isLoading = false
    private var selectedStore: Locations? = null
    private var selectedMall: Locations? = null
    private var isScheduleViewOpen = false

    lateinit var subscribeOnClickOfferEvent: Disposable

    private val stateObserver = Observer<StorePageState> { state ->
        state?.let {
            isLoading = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false
                    if (state.voucherList.isNotEmpty()) {
                        if (state.voucherList.size >= 3) {
                            tv_show_all_offers_button.visibility = View.VISIBLE
                            initializeVouchersRecyclerView(getFirstTwoVoucher(state.voucherList))
                        } else {
                            tv_show_all_offers_button.visibility = View.GONE
                            initializeVouchersRecyclerView(state.voucherList)
                        }
                    } else {
                        tv_show_all_offers_button.visibility = View.GONE
                        tv_offers_title.visibility = View.GONE
                        rv_store_offers.visibility = View.GONE
                    }
                    fillPageData()


                }
                is LoadingState -> {
                    isLoading = true
                }
                is ErrorState -> {
                    isLoading = false
                    tv_offers_title.visibility = View.GONE
                    rv_store_offers.visibility = View.GONE
                    tv_show_all_offers_button.visibility = View.GONE
                    fillPageData()
                }
            }
        }
    }

    private fun getFirstTwoVoucher(voucherList: ArrayList<Voucher>): ArrayList<Voucher> {
        val filteredArray = mutableListOf<Voucher>()
        filteredArray.add(voucherList[0])
        filteredArray.add(voucherList[1])
        filteredArray.add(voucherList[2])
        return ArrayList(filteredArray)

    }

    companion object {
        fun newInstance(
            selectedStore: Locations?,
            selectedMall: Locations?
        ): StoreFragment {
            val f = StoreFragment()

            val args = Bundle()
            args.putParcelable("selectedStore", selectedStore)
            args.putParcelable("selectedMall", selectedMall)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedStore = arguments?.getParcelable("selectedStore")
        selectedMall = arguments?.getParcelable("selectedMall")
        subscribe()
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
        return inflater.inflate(R.layout.store_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cl_open_close_hours.visibility = View.GONE
        cl_preLoading.visibility = View.VISIBLE
        observeViewModel()
        subscribe()
        initListener()
        viewModel.getAllVouchers(selectedStore!!)

    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)

        if (subscribeOnClickOfferEvent.isDisposed) {
            subscribeOnClickOfferEvent.dispose()
        }
    }

    override fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun subscribe() {
        subscribeOnClickOfferEvent =
            RxBus.listen(OnClickOfferEvent::class.java).subscribe { event ->
                activity?.let {
                    val activity: AppCompatActivity = activity as AppCompatActivity
                    if (activity.getCurrentFragment() !is OfferDetailFragment) {
                        activity.replaceFragment(
                            R.id.frame_main_content,
                            OfferDetailFragment.newInstance(event.voucher), true
                        )
                    }
                    subscribeOnClickOfferEvent.dispose()
                }
            }
    }

    private fun fillPageData() {
        selectedStore?.let { location ->
            tv_mall_name.text = location.location_details?.name
            tv_mall_name_sub_title.text = location.city_name
            tv_mall_shop_count.text =
                location.type?.name//getString(R.string.title_shops_count, location.type?.name)
            tv_mall_description.text = location.location_details?.description
            context?.let {
                Glide.with(context!!).load(location.location_details?.picture_url)
                    .into(app_bar_image)

                Glide.with(context!!).load(location.location_details?.logo_url)
                    .into(iv_store_logo)
            }
        }

        cl_preLoading.visibility = View.GONE
    }

    private fun initializeVouchersRecyclerView(
        data: ArrayList<Voucher>
    ) {
        rv_store_offers.apply {
            rv_store_offers.adapter = OffersListAdapter(data,false, OffersListAdapterType.NORMAL)
            rv_store_offers.isNestedScrollingEnabled = false
        }

    }

    private fun initListener() {
        iv_mail_button.setOnClickListener {
            context?.let {
                val activity: AppCompatActivity = it as AppCompatActivity
                activity.sendEmail(selectedStore!!)
            }
        }

        iv_web_button.setOnClickListener {
            context?.let {
                val activity: AppCompatActivity = it as AppCompatActivity

                activity.openUrlAtBrowser(
                    selectedStore?.location_details?.website
                )
            }
        }

        iv_phone_button.setOnClickListener {
            context?.let {
                val activity: AppCompatActivity = it as AppCompatActivity
                if (activity.checkAndRequestPermissions()) {
                    activity.callPhone(selectedStore?.location_details?.phone)
                }
            }
        }

        tv_navigate_to_store.setOnClickListener {
            context?.let {
                val activity: AppCompatActivity = it as AppCompatActivity

                activity.replaceFragment(
                    R.id.frame_main_content,
                    MapFragment.newInstance(
                        selectedMall,
                        selectedStore,
                        null
                    ), true
                )
            }
        }

//        tv_save_checkpoint_button.setOnClickListener {
//
//            context?.let {
//                val activity: AppCompatActivity = it as AppCompatActivity
//
//                activity.replaceFragment(
//                    R.id.frame_main_content,
//                    MapFragment.newInstance(
//                        selectedStore
//                    ), true
//                )
//            }
//
//        }

        tv_locate_on_map_button.setOnClickListener {
            context?.let {
                val activity: AppCompatActivity = it as AppCompatActivity

                activity.replaceFragment(
                    R.id.frame_main_content,
                    MapFragment.newInstance(
                        selectedMall,
                        null,
                        selectedStore
                    ), true
                )
            }
        }

        iv_share_button.setOnClickListener {
            activity?.let {
                val activity: AppCompatActivity = it as AppCompatActivity
                activity.shareLocation(selectedStore!!)
            }
        }

        tv_show_all_offers_button.setOnClickListener {
            context?.let {
                val activity: AppCompatActivity = it as AppCompatActivity

                activity.replaceFragment(
                    R.id.frame_main_content,
                    OffersListFragment.newInstance(
                        selectedStore
                    ), true
                )
            }
        }

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            //  Vertical offset == 0 indicates appBar is fully  expanded.
            if (Math.abs(verticalOffset) == 0) {
                iv_store_logo.visibility = View.VISIBLE
            } else {
                iv_store_logo.visibility = View.GONE
            }
        })

        cl_schedule_time.setOnClickListener {
            if (isScheduleViewOpen) {
                cl_open_close_hours.visibility = View.GONE
                iv_arrow_down.setImageResource(R.drawable.ic_chevron_down_black)
            } else {
                iv_arrow_down.setImageResource(R.drawable.ic_chevron_up)
                cl_open_close_hours.visibility = View.VISIBLE
            }

            isScheduleViewOpen = !isScheduleViewOpen
        }
    }

}