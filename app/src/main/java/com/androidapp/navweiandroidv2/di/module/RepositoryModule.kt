package com.androidapp.navweiandroidv2.di.module

import com.androidapp.data.api.NavweiApi
import dagger.Module
import dagger.Provides
import repository.NavweiRepository
import javax.inject.Singleton

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */
@Module
class RepositoryModule {
    @Provides
    @Singleton
    internal fun provideNavweiRepository(endpoint: NavweiApi): NavweiRepository = NavweiRepository(endpoint)
}