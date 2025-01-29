package com.interstellar.travelInsurance.core.di

import android.content.Context
import android.content.SharedPreferences
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesSharedPref(@ApplicationContext appContext: Context): SharedPreferenceManager {
        return SharedPreferenceManager(appContext)
    }

}