package com.github.i36lib.xtv

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import com.github.i36lib.xtv.data.repositories.EpgRepository
import com.github.i36lib.xtv.data.repositories.EpgRepositoryImpl
import com.github.i36lib.xtv.data.repositories.IptvRepository
import com.github.i36lib.xtv.data.repositories.IptvRepositoryImpl

@HiltAndroidApp
class XtvApplication : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideIptvRepository(context: Context): IptvRepository {
        return IptvRepositoryImpl(context)
    }

    @Provides
    fun provideEpgRepository(context: Context): EpgRepository {
        return EpgRepositoryImpl(context)
    }
}