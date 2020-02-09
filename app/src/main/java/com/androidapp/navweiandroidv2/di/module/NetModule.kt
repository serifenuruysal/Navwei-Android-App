package com.androidapp.navweiandroidv2.di.module

import com.androidapp.data.api.NavweiApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
private const val BASE_URL = "https://app-9e98701c-268c-4413-bbc5-f8b031995794.cleverapps.io/en/"

@Module
class NetModule {

    @Provides
    fun providesCoinMarketCapApi(retrofit: Retrofit) = retrofit.create(NavweiApi::class.java)

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()


    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}