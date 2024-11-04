package com.nhathuy.gas24h_7app.data.api

import com.nhathuy.gas24h_7app.data.model.Notification
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @GET("/all")
    suspend fun getAllNotification() : List<Notification>

    @GET("/{id}")
    suspend fun getNotificationById(@Path("id") id:String) : Notification

    @Multipart
    @POST("/add")
    suspend fun addNotification(@Part("title") title:RequestBody ,@Part("content") content:RequestBody, @Part imageData: MultipartBody.Part,@Part("hotline") hotline:RequestBody)

    @Multipart
    @PUT("/edit/{id}")
    suspend fun editNotification(@Path("id") id:String,@Part("title") title:RequestBody ,@Part("content") content:RequestBody, @Part imageData: MultipartBody.Part,@Part("hotline") hotline:RequestBody)

    @DELETE("/delete/{id}")
    suspend fun deleteNotification(@Path("id") id:String)
}