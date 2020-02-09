package com.androidapp.navweiandroidv2.di.component

import com.androidapp.navweiandroidv2.App
import com.androidapp.navweiandroidv2.di.module.*
import com.androidapp.navweiandroidv2.presentation.parentlocation.di.HomeActivityModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.di.MainActivityModule
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.di.FilterActivityModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

@Singleton
@Component(
    modules = [AppModule::class,
        NetModule::class,
        RepositoryModule::class,
        MallViewModelModule::class,
        HomeViewModelModule::class,
        OffersViewModelModule::class,
        FilterViewModelModule::class,
        ChooseStoreFiltersViewModelModule::class,
        AndroidSupportInjectionModule::class,
        MainActivityModule::class,
        HomeActivityModule::class,
        FilterActivityModule::class,
        ViewModelFactoryModule::class,
        MallCategoryViewModelModule::class,
        StoreViewModelModule::class,
        OfferDetailViewModelModule::class,
        MapViewModelModule::class,
        OffersListViewModelModule::class
    ]
)
interface ApplicationComponent {
    fun inject(app: App)
}