package com.nhathuy.gas24h_7app.data.api

import com.nhathuy.gas24h_7app.util.Constants
import com.nhathuy.gas24h_7app.util.Constants.BASE_URL
import com.nhathuy.gas24h_7app.util.Constants.NOTIFICATION_URL
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object RetrofitClient {

    @LocationRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
             .baseUrl(BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
    }


    @Provides
    @Singleton
    fun provideLocationApi( @LocationRetrofit retrofit: Retrofit): LocationApiService {
        return retrofit.create(LocationApiService::class.java)
    }

    @NotificationRetrofit
    @Provides
    @Singleton
    fun provideNotificationRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NOTIFICATION_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideNotificationApi(@NotificationRetrofit retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
}