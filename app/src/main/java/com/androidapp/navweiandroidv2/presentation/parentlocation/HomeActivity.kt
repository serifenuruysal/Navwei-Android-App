package com.androidapp.navweiandroidv2.presentation.parentlocation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.androidapp.entity.models.Locations
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.BaseActivity
import com.androidapp.navweiandroidv2.presentation.locationdetails.MainActivity
import com.androidapp.navweiandroidv2.presentation.parentlocation.adapter.TutorialPagerAdapter
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.ClickFiltersEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.events.OnClickMallSelectedEvent
import com.androidapp.navweiandroidv2.presentation.parentlocation.filters.HomeFiltersFragment
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.androidapp.navweiandroidv2.util.ext.addFragment
import com.androidapp.navweiandroidv2.util.ext.gone
import com.androidapp.navweiandroidv2.util.helper.SharedPreference
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : BaseActivity(), HasSupportFragmentInjector {

    lateinit var subscribeOnClickMallSelectedEvent: Disposable
    lateinit var subscribeClickFiltersEvent: Disposable

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val sharedPref = SharedPreference(this)
        if (sharedPref.getValue(Constants.IS_FIRST_LAUNCH)!!) {
            sharedPref.save(Constants.IS_FIRST_LAUNCH, false)
            initViewPager()
        } else {
            if (savedInstanceState == null) {
                addFragment(R.id.frame_main_content, HomeFragment(), true)
            }
        }

        subscribe()
    }

    private fun initViewPager() {
        val imageDrawableArrayList: MutableList<Int> = mutableListOf()
        imageDrawableArrayList.add(R.drawable.tuto_1)
        imageDrawableArrayList.add(R.drawable.tuto_2)
        imageDrawableArrayList.add(R.drawable.tuto_3)
        imageDrawableArrayList.add(R.drawable.tuto_4)
        vp_tutorial?.adapter =
            TutorialPagerAdapter(
                this,
                imageDrawableArrayList
            )

        vp_tutorial.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == imageDrawableArrayList.size - 1) {
                    vp_tutorial.setOnTouchListener { _, _ ->
                        vp_tutorial.gone()
                        addHomeFragment()
                        return@setOnTouchListener true
                    }
                }
            }
        })
    }

    private fun addHomeFragment() {
        addFragment(R.id.frame_main_content, HomeFragment(), true)
    }

    private fun subscribe() {
        subscribeOnClickMallSelectedEvent =
            RxBus.listen(OnClickMallSelectedEvent::class.java).subscribe { result ->
                startMainActivity(result.mall)
            }

        subscribeClickFiltersEvent =
            RxBus.listen(ClickFiltersEvent::class.java).subscribe {
                addFragment(
                    R.id.frame_main_content,
                    HomeFiltersFragment.newInstance(
                        it.selectedOption, it.listType, it.listMall
                    )
                    , true
                )
            }
    }

    private fun startMainActivity(store: Locations) {
        val i = Intent(this@HomeActivity, MainActivity::class.java)
        i.putExtra(Constants.STORE, store)
        startActivity(i)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!subscribeOnClickMallSelectedEvent.isDisposed) {
            subscribeOnClickMallSelectedEvent.dispose()
        }
        if (!subscribeClickFiltersEvent.isDisposed) {
            subscribeClickFiltersEvent.dispose()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()

        val fragmentCount = supportFragmentManager.backStackEntryCount
        if (fragmentCount == 0) {
            finish()
        } else {
            if (fragmentCount > 0) {
                // TODO: Depracated
                fragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }

}

class Constants {
    companion object {
        const val STORE: String = "STORE"
        const val IS_FIRST_LAUNCH: String = "is_first_launch"
    }
}
