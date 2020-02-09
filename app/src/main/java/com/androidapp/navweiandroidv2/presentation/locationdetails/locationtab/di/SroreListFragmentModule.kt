package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.storelist.StoreListFragment
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
interface MallCategoryFragmentSubcomponent : AndroidInjector<StoreListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<StoreListFragment>()
}

@Module(subcomponents = [MallCategoryFragmentSubcomponent::class])
abstract class MallCategoryFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(StoreListFragment::class)
    abstract fun bindMallCategoryFragmentSubcomponentInjectorFactory(builder: MallCategoryFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}