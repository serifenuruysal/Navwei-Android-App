package com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab.MoreFragment
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
interface MoreFragmentSubcomponent : AndroidInjector<MoreFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MoreFragment>()
}

@Module(subcomponents = [MoreFragmentSubcomponent::class])
abstract class MoreFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MoreFragment::class)
    abstract fun bindMoreFragmentSubcomponentInjectorFactory(builder: MoreFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}