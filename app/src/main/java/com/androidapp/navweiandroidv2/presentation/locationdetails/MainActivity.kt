package com.androidapp.navweiandroidv2.presentation.locationdetails

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidapp.entity.models.Locations
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseActivity
import com.androidapp.navweiandroidv2.presentation.parentlocation.Constants.Companion.STORE
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events.OnClickOpenOfferFragmentEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.mall.MallFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.MapFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab.MoreFragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.OffersFragment
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.getCurrentFragment
import com.androidapp.navweiandroidv2.util.ext.replaceFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import javax.inject.Inject

class MainActivity : BaseActivity(), HasSupportFragmentInjector {

    var selectedMall: Locations? = null

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    private lateinit var subscribeOnClickOpenOfferFragmentEvent: Disposable

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectedMall = intent.getParcelableExtra(STORE)

//        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            onClickMallTab(null)
        }

        subscribe()
    }

    fun onClickMallTab(v: View?) {
        deselectALlTabItem()
        iv_mall_item.setImageResource(R.drawable.ic_tab_mall_active)
        iv_mall_title.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        onBottomNavigationClickHandler(R.id.navigation_mall)
    }

    fun onClickOfferTab(v: View?) {
        deselectALlTabItem()
        iv_offer_item.setImageResource(R.drawable.ic_tab_offers_active)
        iv_offer_title.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        onBottomNavigationClickHandler(R.id.navigation_mall_offers)
    }

    fun onClickMapTab(v: View?) {
        deselectALlTabItem()
        iv_map_item.setImageResource(R.drawable.ic_tab_map_active)
        iv_map_title.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        onBottomNavigationClickHandler(R.id.navigation_mall_map)
    }

    fun onClickMoreTab(v: View?) {
        deselectALlTabItem()
        iv_more_item.setImageResource(R.drawable.ic_tab_more_active)
        iv_more_title.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        onBottomNavigationClickHandler(R.id.navigation_mall_more)
    }

    private fun deselectALlTabItem() {
        iv_offer_item.setImageResource(R.drawable.ic_tab_offers)
        iv_mall_item.setImageResource(R.drawable.ic_tab_mall)
        iv_map_item.setImageResource(R.drawable.ic_tab_map)
        iv_more_item.setImageResource(R.drawable.ic_tab_more)

        iv_offer_title.setTextColor(ContextCompat.getColor(this, R.color.light_text_color))
        iv_mall_title.setTextColor(ContextCompat.getColor(this, R.color.light_text_color))
        iv_map_title.setTextColor(ContextCompat.getColor(this, R.color.light_text_color))
        iv_more_title.setTextColor(ContextCompat.getColor(this, R.color.light_text_color))
    }

    private fun onBottomNavigationClickHandler(itemId: Int) {
        when (itemId) {
            R.id.navigation_mall -> {
                if (getCurrentFragment() !is MallFragment) {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    replaceFragment(
                        R.id.frame_main_content,
                        MallFragment.newInstance(selectedMall), true
                    )
                }
            }
            R.id.navigation_mall_offers -> {
                if (getCurrentFragment() !is OffersFragment) {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    replaceFragment(
                        R.id.frame_main_content,
                        OffersFragment.newInstance(selectedMall),
                        true
                    )
                }

            }
            R.id.navigation_mall_map -> {
                if (getCurrentFragment() !is MapFragment) {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    replaceFragment(
                        R.id.frame_main_content,
                        MapFragment.newInstance(selectedMall,null,null),
                        true
                    )
                }
            }
            R.id.navigation_mall_more -> {
                if (getCurrentFragment() !is MoreFragment) {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    replaceFragment(
                        R.id.frame_main_content,
                        MoreFragment.newInstance(selectedMall),
                        true
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun subscribe() {
        subscribeOnClickOpenOfferFragmentEvent =
            RxBus.listen(OnClickOpenOfferFragmentEvent::class.java).subscribe {
                onClickOfferTab(null)
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!subscribeOnClickOpenOfferFragmentEvent.isDisposed) {
            subscribeOnClickOpenOfferFragmentEvent.dispose()
        }
    }

}
