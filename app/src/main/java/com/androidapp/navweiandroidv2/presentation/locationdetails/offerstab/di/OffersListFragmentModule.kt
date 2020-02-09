package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerlist.OffersListFragment
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
interface OffersListFragmentSubcomponent : AndroidInjector<OffersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<OffersListFragment>()
}

@Module(subcomponents = [OffersListFragmentSubcomponent::class])
abstract class OffersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(OffersListFragment::class)
    abstract fun bindOffersListFragmentSubcomponentInjectorFactory(builder: OffersListFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}