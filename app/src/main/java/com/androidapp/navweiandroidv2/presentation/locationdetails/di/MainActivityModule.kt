package com.androidapp.navweiandroidv2.presentation.locationdetails.di

import android.app.Activity
import com.androidapp.navweiandroidv2.presentation.locationdetails.MainActivity
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.di.MallCategoryFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.di.MallFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.di.StoreFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.di.MapFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.settingstab.di.MoreFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.di.OfferDetailFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.di.OffersFragmentModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.di.OffersListFragmentModule
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap


/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
@Module(subcomponents = [MainActivitySubcomponent::class])
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    abstract fun bindMainActivityInjectorFactory(builder: MainActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent(
    modules = [
        MallFragmentModule::class,
        MoreFragmentModule::class,
        OffersFragmentModule::class,
        MallCategoryFragmentModule::class,
        StoreFragmentModule::class,
        OfferDetailFragmentModule::class,
        MapFragmentModule::class,
        OffersListFragmentModule::class
    ]
)
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}