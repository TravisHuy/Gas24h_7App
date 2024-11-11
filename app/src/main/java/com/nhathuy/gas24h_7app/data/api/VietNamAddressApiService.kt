package com.nhathuy.gas24h_7app.data.api

import com.nhathuy.gas24h_7app.data.model.Districts
import com.nhathuy.gas24h_7app.data.model.Provinces
import com.nhathuy.gas24h_7app.data.model.Wards
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VietNamAddressApiService {

    @GET("/api/travishuy/vietnamaddress/provinces")
    suspend fun getAllProvinces(): Response<List<Provinces>>

    @GET("/api/travishuy/vietnamaddress/provinces/{provinceId}/districts")
    suspend fun getDistrictsByProvinceId(@Path("provinceId") provinceId: String): Response<List<Districts>>

    @GET("/api/travishuy/vietnamaddress/districts/{districtId}/wards")
    suspend fun getWardsByDistrictId(@Path("districtId") districtId: String): Response<List<Wards>>
}