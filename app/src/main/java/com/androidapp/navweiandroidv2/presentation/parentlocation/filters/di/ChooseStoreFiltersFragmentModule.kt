package com.androidapp.navweiandroidv2.presentation.parentlocation.filters.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.parentlocation.filters.HomeFiltersFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

/**
 * Created by S.Nur Uysal on 2019-11-05.
 */

@Subcomponent/*(modules = ...)*/
interface ChooseStoreFiltersFragmentSubcomponent : AndroidInjector<HomeFiltersFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeFiltersFragment>()
}

@Module(subcomponents = [ChooseStoreFiltersFragmentSubcomponent::class])
abstract class ChooseStoreFiltersFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(HomeFiltersFragment::class)
    abstract fun bindChooseStoreFiltersFragmentSubcomponentInjectorFactory(builder: ChooseStoreFiltersFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}