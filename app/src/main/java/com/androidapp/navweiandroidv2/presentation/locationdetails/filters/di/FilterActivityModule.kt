package com.androidapp.navweiandroidv2.presentation.locationdetails.filters.di

import android.app.Activity
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.FiltersActivity
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap


/**
 * Created by S.Nur Uysal on 2019-10-23.
 */


@Module(subcomponents = [FilterActivitySubcomponent::class])
abstract class FilterActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(FiltersActivity::class)
    abstract fun bindFilterActivityInjectorFactory(builder: FilterActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent(modules = [FilterMainFragmentModule::class])
interface FilterActivitySubcomponent : AndroidInjector<FiltersActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FiltersActivity>()
}