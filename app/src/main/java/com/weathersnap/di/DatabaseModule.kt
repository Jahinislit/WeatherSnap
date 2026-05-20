package com.weathersnap.di

import android.content.Context
import androidx.room.Room
import com.weathersnap.data.local.CachedCityDao
import com.weathersnap.data.local.ReportDao
import com.weathersnap.data.local.WeatherSnapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherSnapDatabase {
        return Room.databaseBuilder(
            context,
            WeatherSnapDatabase::class.java,
            "weathersnap_db"
        ).build()
    }

    @Provides
    fun provideReportDao(db: WeatherSnapDatabase): ReportDao = db.reportDao()

    @Provides
    fun provideCachedCityDao(db: WeatherSnapDatabase): CachedCityDao = db.cachedCityDao()
}
