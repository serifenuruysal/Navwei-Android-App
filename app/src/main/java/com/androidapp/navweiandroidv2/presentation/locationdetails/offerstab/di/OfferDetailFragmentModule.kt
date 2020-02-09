package com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail.OfferDetailFragment
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
interface OfferDetailFragmentSubcomponent : AndroidInjector<OfferDetailFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<OfferDetailFragment>()
}

@Module(subcomponents = [OfferDetailFragmentSubcomponent::class])
abstract class OfferDetailFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(OfferDetailFragment::class)
    abstract fun bindOfferDetailFragmentSubcomponentInjectorFactory(builder: OfferDetailFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}