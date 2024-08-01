package com.nhathuy.gas24h_7app.data.model

data class Product (
    val id: Int,
    val name:String,
    val category:String,
    val description:String ,
    val price:Double,
    val offerPercentage :Double ?=null,
    val imageUrl:String,
)