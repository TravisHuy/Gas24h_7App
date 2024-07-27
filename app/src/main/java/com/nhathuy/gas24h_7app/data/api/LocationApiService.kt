package com.nhathuy.gas24h_7app.data.api

import com.nhathuy.gas24h_7app.data.response.AddressResponse
import com.nhathuy.gas24h_7app.data.response.DistrictResponse
import com.nhathuy.gas24h_7app.data.response.WardResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationApiService {

    @GET("api-tinhthanh/1/0.htm")
    suspend fun getProvides(): AddressResponse

    @GET("api-tinhthanh/2/{provinceId}.htm")
    suspend fun getDistricts(@Path("provinceId") provinceId: String): DistrictResponse

    @GET("api-tinhthanh/3/{districtId}.htm")
    suspend fun getWards(@Path("districtId") districtId: String): WardResponse
}