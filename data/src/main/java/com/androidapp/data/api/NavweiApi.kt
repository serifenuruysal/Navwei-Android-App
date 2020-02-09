package com.androidapp.data.api

import CategoryResponse
import LocationResponse
import LocationsResponse
import com.androidapp.entity.models.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by S.Nur Uysal on 2019-10-21.
 */
interface NavweiApi {

    @GET("location/{mall_id}/store")
    fun getStoresByMallId(@Path("mall_id") locationId: String): Single<LocationsResponse>

    @GET("location/{floor_id}/store")
    fun getStoresByFloorId(@Path("floor_id") floor_id: String): Single<LocationsResponse>

    @GET("location")
    fun getMallLocations(): Single<LocationsResponse>

    @GET("location/{store_id}")
    fun getStoresByStoreId(@Path("store_id") locationId: String): Single<LocationResponse>

    @GET("category")
    fun getCategories(): Single<CategoryResponse>

    @GET("country")
    fun getCountries(): Single<CountryResponse>

    @GET("city")
    fun getCities(): Single<CityResponse>

    @GET("type")
    fun getTypes(): Single<TypeResponse>

    @GET("location/{mall_id}/floor")
    fun getFloorsByMallId(@Path("mall_id") locationId: String): Single<LocationsResponse>

    @GET("voucher/location/{store_id}")
    fun getVoucherByStoreId(@Path("store_id") locationId: String): Single<VoucherResponse>

    @GET("voucher/location/{mall_id}?type=parent")
    fun getAllVouchersByMallId(@Path("mall_id") locationId: String): Single<VoucherResponse>

    @GET("location/{floor_id}/map")
    fun getMapByFloorId(@Path("floor_id") floorId: String): Single<MapResponse>

    @GET("slider/location/{location_id}")
    fun getSlider(@Path("location_id") locationId: String): Single<SliderResponse>

}
