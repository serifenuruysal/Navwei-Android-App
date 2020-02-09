package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.MapFragment
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
interface MapFragmentSubcomponent : AndroidInjector<MapFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MapFragment>()
}

@Module(subcomponents = [MapFragmentSubcomponent::class])
abstract class MapFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MapFragment::class)
    abstract fun bindMapFragmentSubcomponentInjectorFactory(builder: MapFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}