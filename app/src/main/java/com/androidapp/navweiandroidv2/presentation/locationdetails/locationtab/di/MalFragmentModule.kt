package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.di

import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.mall.MallFragment
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
interface MallFragmentSubcomponent : AndroidInjector<MallFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MallFragment>()
}

@Module(subcomponents = [MallFragmentSubcomponent::class])
abstract class MallFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MallFragment::class)
    abstract fun bindMallFragmentSubcomponentInjectorFactory(builder: MallFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>
}