package com.nhathuy.gas24h_7app.data.response

import com.nhathuy.gas24h_7app.data.model.Ward

data class WardResponse(
    val error: Int,
    val errorText: String,
    val dataName: String,
    val data: List<Ward>
)
