package repository

import CategoryResponse
import LocationResponse
import LocationsResponse
import android.transition.Slide
import com.androidapp.entity.models.*
import io.reactivex.Single

/**
 * Created by S.Nur Uysal on 2019-10-21.
 */

interface NavweiRepositoryImpl {

    fun getStoresByMallId(locationId: String): Single<LocationsResponse>

    fun getStoresByFloorId(floorId: String): Single<LocationsResponse>

    fun getMallLocations(): Single<LocationsResponse>

    fun getStoresByStoreId(locationId: String): Single<LocationResponse>

    fun getCategories(): Single<CategoryResponse>

    fun getCountries(): Single<CountryResponse>

    fun getCities(): Single<CityResponse>

    fun getTypes(): Single<TypeResponse>

    fun getFloorsByMallId(locationId: String): Single<LocationsResponse>

    fun getVoucherByStoreId(locationId: String): Single<VoucherResponse>

    fun getAllVouchersByMallId(locationId: String): Single<VoucherResponse>

    fun getMapByFloorId(floorId: String): Single<MapResponse>

    fun getSlider(locationId: String):Single<SliderResponse>

}
