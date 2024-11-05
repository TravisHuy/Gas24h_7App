package com.nhathuy.gas24h_7app.data.repository

import com.nhathuy.gas24h_7app.data.api.NotificationApiService
import com.nhathuy.gas24h_7app.data.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val notificationApiService: NotificationApiService) {

    suspend fun getAllNotifications(): Flow<Result<List<Notification>>> = flow {
        try {
            val response = notificationApiService.getAllNotification()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("Empty response body")))
            } else {
                emit(Result.failure(Exception("Failed to fetch notifications")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getNotificationById(id: String): Flow<Result<Notification>> = flow {
        try {
            val response = notificationApiService.getNotificationById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("Empty response body")))
            } else {
                emit(Result.failure(Exception("Failed to fetch notifications")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun addNotification(
        title: RequestBody,
        content: RequestBody,
        imageFile: MultipartBody.Part?,
        hotline: RequestBody,
    ): Flow<Result<Unit>> = flow {
        try {
            val response = notificationApiService.addNotification(
                title,
                content,
                imageFile,
                hotline
            )

            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to add notification: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun editNotification(
        id: String,
        title: RequestBody,
        content: RequestBody,
        imageFile: MultipartBody.Part?,
        hotline: RequestBody,
    ): Flow<Result<Unit>> = flow {
        try {
            val response = notificationApiService.editNotification(
                id,
                title,
                content,
                imageFile,
                hotline
            )

            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to add notification: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun deleteNotification(id: String): Flow<Result<Unit>> = flow {
        try {
            val response = notificationApiService.deleteNotification(id)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to delete notification")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

}