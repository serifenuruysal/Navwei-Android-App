package com.androidapp.navweiandroidv2.presentation.parentlocation.di

import android.app.Activity
import com.androidapp.navweiandroidv2.presentation.parentlocation.HomeActivity
import com.androidapp.navweiandroidv2.presentation.parentlocation.filters.di.ChooseStoreFiltersFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.di.HomeFragmentModule
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap


/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
@Module(subcomponents = [HomeActivitySubcomponent::class])
abstract class HomeActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(HomeActivity::class)
    abstract fun bindHomeActivityInjectorFactory(builder: HomeActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent(modules = [HomeFragmentModule::class, ChooseStoreFiltersFragmentModule::class])
interface HomeActivitySubcomponent : AndroidInjector<HomeActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeActivity>()
}