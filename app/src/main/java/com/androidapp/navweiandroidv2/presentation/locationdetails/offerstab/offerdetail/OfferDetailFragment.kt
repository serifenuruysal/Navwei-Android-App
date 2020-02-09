package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail

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
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.adapter.StoreListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.adapters.OffersListAdapterType
import com.androidapp.navweiandroidv2.util.ext.formatDate
import com.androidapp.navweiandroidv2.util.ext.shareVoucher
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.offer_detail_fragment.*
import kotlinx.android.synthetic.main.offer_detail_fragment.app_bar_image
import kotlinx.android.synthetic.main.offer_detail_fragment.appbar
import kotlinx.android.synthetic.main.offer_detail_fragment.cl_preLoading
import kotlinx.android.synthetic.main.offer_detail_fragment.rv_store_offers
import kotlinx.android.synthetic.main.offer_detail_fragment.toolbar
import javax.inject.Inject
import kotlin.math.abs

/**
 * Created by S.Nur Uysal on 2019-11-26.
 */
class OfferDetailFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: OfferDetailViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OfferDetailViewModel::class.java)
    }

    private var isLoading = false
    private var selectedVoucher: Voucher? = null

    private val stateObserver = Observer<OfferDetailPageState> { state ->
        state?.let {
            isLoading = state.loadedAllItems
            when (state) {
                is DefaultState -> {
                    isLoading = false
                    if (state.voucherList.isNotEmpty()) {
                        tv_more_offers.visibility = View.VISIBLE
                        initializeVouchersRecyclerView(state.voucherList)
                    } else {
                        tv_more_offers.visibility = View.GONE
                    }
                    if (state.locationList.isNotEmpty()) {
                        initializeStoreRecyclerView(state.locationList)
                    }
                    fillPageData()
                }
                is LoadingState -> {
                    isLoading = true
                }
                is ErrorState -> {
                    isLoading = false
                    fillPageData()
                }
            }
        }
    }

    companion object {
        fun newInstance(selectedVoucher: Voucher?): OfferDetailFragment {
            val f = OfferDetailFragment()
            val args = Bundle()

            args.putParcelable("SelectedVoucher", selectedVoucher)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedVoucher = arguments?.getParcelable("SelectedVoucher")
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
        return inflater.inflate(R.layout.offer_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        iv_offer_logo.visibility = View.GONE
        cl_preLoading.visibility = View.VISIBLE

        if (selectedVoucher != null) {
            viewModel.getStoreByVoucher(selectedVoucher!!)
            viewModel.getVouchersOfSameStore(selectedVoucher!!)
        }
        initListeners()
    }

    private fun initListeners(){
        iv_share_button_offer.setOnClickListener {
            activity?.let {
                val activity: AppCompatActivity = it as AppCompatActivity
                activity.shareVoucher(selectedVoucher!!)
            }
        }

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, vOffset ->
            //  Vertical offset == 0 indicates appBar is fully  expanded.
            if (abs(vOffset) == 0) {
                iv_offer_logo.visibility = View.VISIBLE
            } else {
                iv_offer_logo.visibility = View.GONE
            }
        })
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)
    }

    override fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun fillPageData() {
        selectedVoucher?.let { voucher ->
            tv_offer_name.text = voucher.name
            tv_offer_name_sub_title.text = context?.getString(com.androidapp.navweiandroidv2.R.string.title_expire,voucher.expired_at?.formatDate())
            tv_offer_description.text = voucher.name
            context?.let {
                Glide.with(context!!).load(voucher.cover_url)
                    .into(iv_offer_logo)

                Glide.with(context!!).load(voucher.picture_url)
                    .into(app_bar_image)
            }
        }

        cl_preLoading.visibility = View.GONE
    }

    private fun initializeVouchersRecyclerView(data: ArrayList<Voucher>) {
        rv_store_offers.apply {
            rv_store_offers.adapter = OffersListAdapter(data,false, OffersListAdapterType.NORMAL)
            rv_store_offers.isNestedScrollingEnabled=false
        }
    }

    private fun initializeStoreRecyclerView(data: List<Locations>) {
        rv_store.apply {
            rv_store.adapter = StoreListAdapter(data)
            rv_store.isNestedScrollingEnabled=false
        }
    }

}
