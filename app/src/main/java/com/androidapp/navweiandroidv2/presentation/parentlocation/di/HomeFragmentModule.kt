package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.parentlocation.HomeFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

@Subcomponent/*(modules = ...)*/
interface HomeFragmentSubcomponent : AndroidInjector<HomeFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeFragment>()
}

@Module(subcomponents = [HomeFragmentSubcomponent::class])
abstract class HomeFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(HomeFragment::class)
    abstract fun bindHomeFragmentSubcomponentInjectorFactory(builder: HomeFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}