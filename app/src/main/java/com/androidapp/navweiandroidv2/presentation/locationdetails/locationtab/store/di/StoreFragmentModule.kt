package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.StoreFragment
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
interface StoreFragmentSubcomponent : AndroidInjector<StoreFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<StoreFragment>()
}

@Module(subcomponents = [StoreFragmentSubcomponent::class])
abstract class StoreFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(StoreFragment::class)
    abstract fun bindStoreFragmentSubcomponentInjectorFactory(builder: StoreFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}