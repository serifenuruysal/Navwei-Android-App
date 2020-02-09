package com.androidapp.navweiandroidv2.di.module

import androidx.lifecycle.ViewModel
import com.androidapp.navweiandroidv2.presentation.parentlocation.HomeViewModel
import com.androidapp.navweiandroidv2.presentation.parentlocation.filters.HomeFiltersViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.FilterViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.mall.MallViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.store.StoreViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.storelist.StoreListViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.MapViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.OffersViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerdetail.OfferDetailViewModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.offerstab.offerlist.OffersListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)


@Module
abstract class MallViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MallViewModel::class)
    internal abstract fun bindMallViewModel(viewModel: MallViewModel): ViewModel
}

@Module
abstract class HomeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel
}

@Module
abstract class ChooseStoreFiltersViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeFiltersViewModel::class)
    internal abstract fun bindChooseStoreFiltersViewModel(viewModel: HomeFiltersViewModel): ViewModel
}

@Module
abstract class FilterViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FilterViewModel::class)
    internal abstract fun bindFilterViewModel(viewModel: FilterViewModel): ViewModel
}

@Module
abstract class OffersViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(OffersViewModel::class)
    internal abstract fun bindOffersViewModel(viewModel: OffersViewModel): ViewModel
}

@Module
abstract class OffersListViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(OffersListViewModel::class)
    internal abstract fun bindOffersListViewModel(viewModel: OffersListViewModel): ViewModel
}



@Module
abstract class MallCategoryViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StoreListViewModel::class)
    internal abstract fun bindMallCategoryViewModel(viewModel: StoreListViewModel): ViewModel
}


@Module
abstract class StoreViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StoreViewModel::class)
    internal abstract fun bindStoreViewModelModule(viewModel: StoreViewModel): ViewModel
}

@Module
abstract class OfferDetailViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(OfferDetailViewModel::class)
    internal abstract fun bindOfferDetailViewModelModule(viewModel: OfferDetailViewModel): ViewModel
}

@Module
abstract class MapViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    internal abstract fun bindMapViewModelModule(viewModel: MapViewModel): ViewModel
}






