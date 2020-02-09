package com.androidapp.navweiandroidv2.presentation.locationdetails.filters.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.FilterMainFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

/**
 * Created by S.Nur Uysal on 2019-11-07.
 */


@Subcomponent/*(modules = ...)*/
interface FilterMainFragmentSubcomponent : AndroidInjector<FilterMainFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FilterMainFragment>()
}

@Module(subcomponents = [FilterMainFragmentSubcomponent::class])
abstract class FilterMainFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(FilterMainFragment::class)
    abstract fun bindFilterMainFragmentSubcomponentInjectorFactory(builder: FilterMainFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}