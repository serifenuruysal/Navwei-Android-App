package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerlist

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
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapterType
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.events.OnClickOfferEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail.OfferDetailFragment
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.replaceFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.offers_list_fragment.*
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

class OffersListFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: OffersListViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OffersListViewModel::class.java)
    }

    lateinit var subscribeOnClickOfferEvent: Disposable

    private var isLoading = false

    private var selectedStore: Locations? = null

    private val stateObserver = Observer<OffersListPageState> { state ->
        state?.let {
            isLoading = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false

                    if (state.voucherList.isNotEmpty()) {
                        rv_store_offers.visibility = View.VISIBLE
                        initializeVouchersRecyclerView(ArrayList(state.voucherList))
                        cl_preLoading.visibility = View.GONE

                    } else {
                        rv_store_offers.visibility = View.GONE
                        cl_preLoading.visibility = View.GONE
                    }
                }
                is LoadingState -> {
                    isLoading = true
                }
                is ErrorState -> {
                    cl_preLoading.visibility = View.GONE
                    isLoading = false
                }
            }
        }
    }

    companion object {
        fun newInstance(
            selectedStore: Locations?
        ): OffersListFragment {
            val f = OffersListFragment()

            val args = Bundle()
            args.putParcelable("selectedStore", selectedStore)
            f.arguments = args

            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.offers_list_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedStore = arguments?.getParcelable<Locations>("selectedStore")
        subscribe()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)

        if (subscribeOnClickOfferEvent.isDisposed) {
            subscribeOnClickOfferEvent.dispose()
        }
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe()
        observeViewModel()
        cl_preLoading.visibility = View.VISIBLE

        viewModel.getVoucherByStoreId(selectedStore?.id!!)

        setToolbarTitle(getString(R.string.title_store_offer))
    }

    private fun subscribe() {
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
    }

    private fun initializeVouchersRecyclerView(
        data: ArrayList<Voucher>
    ) {
        rv_store_offers.apply {
            rv_store_offers.adapter = OffersListAdapter(data, true,OffersListAdapterType.NORMAL)
        }
    }

    override fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

}
