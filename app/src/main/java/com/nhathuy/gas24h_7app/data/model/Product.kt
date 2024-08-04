package com.nhathuy.gas24h_7app.data.model

data class Product (
    val id: String,
    var name:String,
    var category:ProductCategory,
    var description:String,
    var price:Double,
    var offerPercentage :Double =0.0,
    var imageUrl:List<String>,
    var reviewCount: Int = 0,
    var averageRating: Float =0.0f
){
    fun toMap():Map<String,Any>{
        return hashMapOf("id" to id,
                        "name" to name,
                        "category" to category.categoryName,
                        "description" to description,
                        "price" to price,
                        "offerPercentage" to offerPercentage,
                        "imageUrl" to imageUrl,
                        "reviews" to reviewCount,
                        "averageRating" to averageRating)
    }

}