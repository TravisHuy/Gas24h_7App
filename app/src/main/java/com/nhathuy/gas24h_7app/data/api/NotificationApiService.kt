package com.nhathuy.gas24h_7app.data.api

import com.nhathuy.gas24h_7app.data.model.Notification
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @GET("travishuy/notifications/all")
    suspend fun getAllNotification() : Response<List<Notification>>

    @GET("travishuy/notifications/{id}")
    suspend fun getNotificationById(@Path("id") id:String) : Response<Notification>

    @Multipart
    @POST("travishuy/notifications/add")
    suspend fun addNotification(@Part("title") title:RequestBody ,@Part("content") content:RequestBody, @Part imageData: MultipartBody.Part?,@Part("hotline") hotline:RequestBody):Response<Unit>

    @Multipart
    @PUT("travishuy/notifications/edit/{id}")
    suspend fun editNotification(@Path("id") id:String,@Part("title") title:RequestBody ,@Part("content") content:RequestBody, @Part imageData: MultipartBody.Part?,@Part("hotline") hotline:RequestBody):Response<Unit>

    @DELETE("travishuy/notifications/delete/{id}")
    suspend fun deleteNotification(@Path("id") id:String):Response<Unit>
}