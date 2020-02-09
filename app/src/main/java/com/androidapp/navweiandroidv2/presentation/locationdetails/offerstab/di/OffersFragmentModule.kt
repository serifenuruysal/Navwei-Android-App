package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.OffersFragment
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
interface OffersFragmentSubcomponent : AndroidInjector<OffersFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<OffersFragment>()
}

@Module(subcomponents = [OffersFragmentSubcomponent::class])
abstract class OffersFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(OffersFragment::class)
    abstract fun bindOffersFragmentSubcomponentInjectorFactory(builder: OffersFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}