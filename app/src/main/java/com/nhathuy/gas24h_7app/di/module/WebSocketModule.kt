package com.nhathuy.gas24h_7app.di.module

import android.content.Context
import com.nhathuy.gas24h_7app.data.helper.NotificationHelper
import com.nhathuy.gas24h_7app.websocket.WebSocketService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class WebSocketModule {

    @Provides
    @Singleton
    fun provideNotificationHelper(context:Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideWebSocket(notificationHelper: NotificationHelper): WebSocketService {
        return WebSocketService(notificationHelper)
    }
}