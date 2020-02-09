package repository

import CategoryResponse
import LocationResponse
import LocationsResponse
import com.androidapp.data.api.NavweiApi
import com.androidapp.entity.models.*
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-30.
 */
class NavweiRepository @Inject constructor(private val navweiApi: NavweiApi) : NavweiRepositoryImpl {
    override fun getMapByFloorId(floorId: String): Single<MapResponse> {
        return navweiApi.getMapByFloorId(floorId)
    }

    override fun getTypes(): Single<TypeResponse> {
        return navweiApi.getTypes()
    }

    override fun getCountries(): Single<CountryResponse> {
        return navweiApi.getCountries()
    }

    override fun getCities(): Single<CityResponse> {
        return navweiApi.getCities()
    }

    override fun getCategories(): Single<CategoryResponse> {
        return navweiApi.getCategories()
    }

    override fun getMallLocations(): Single<LocationsResponse> {
        return navweiApi.getMallLocations()
    }

    override fun getStoresByStoreId(locationId: String): Single<LocationResponse> {
        return navweiApi.getStoresByStoreId(locationId)
    }

    override fun getStoresByMallId(locationId: String): Single<LocationsResponse> {
        return navweiApi.getStoresByMallId(locationId)
    }

    override fun getStoresByFloorId(floorId: String): Single<LocationsResponse> {
        return navweiApi.getStoresByFloorId(floorId)
    }

    override fun getFloorsByMallId(locationId: String): Single<LocationsResponse> {
        return navweiApi.getFloorsByMallId(locationId)
    }

    override fun getVoucherByStoreId(locationId: String): Single<VoucherResponse> {
        return navweiApi.getVoucherByStoreId(locationId)
    }

    override fun getAllVouchersByMallId(locationId: String): Single<VoucherResponse> {
        return navweiApi.getAllVouchersByMallId(locationId)
    }

    override fun getSlider(locationId: String): Single<SliderResponse> {
        return navweiApi.getSlider(locationId)
    }

}
