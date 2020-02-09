package com.androidapp.navweiandroidv2.di.module

import androidx.lifecycle.ViewModelProvider
import com.androidapp.navweiandroidv2.di.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindDaggerViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}