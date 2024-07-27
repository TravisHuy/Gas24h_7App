package com.nhathuy.gas24h_7app.data.model

interface Location {
    val id: String
    val full_name: String
}
data class Province(
    override val id: String,
    override val full_name: String,
):Location

data class District(
    override val id: String,
    override val full_name: String,
) : Location

data class Ward(
    override val id: String,
    override val full_name: String
) : Location