package com.nhathuy.gas24h_7app.data.model

data class Product (
    val id: String,
    val name:String,
    val category:ProductCategory,
    val description:String,
    val price:Double,
    val offerPercentage :Double ?=null,
    val imageUrl:List<String>,
    val reviews: List<Review> = emptyList(),
    val averageRating: Float =0.0f
){

}