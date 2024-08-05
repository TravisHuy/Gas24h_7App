package com.nhathuy.gas24h_7app.data.model

data class Product (
    val id: String,
    var categoryId:String,
    var name:String,
    var description:String,
    var price:Double,
    var offerPercentage :Double,
    var coverImageUrl:String,
    var detailImageUrls:List<String>,
    var reviewCount: Int = 0,
    var averageRating: Float =0.0f
){
    fun toMap():Map<String,Any>{
        return hashMapOf("id" to id,
                        "name" to name,
                        "category" to categoryId,
                        "description" to description,
                        "price" to price,
                        "offerPercentage" to offerPercentage,
                        "coverImageUrl" to coverImageUrl,
                        "detailImageUrls" to detailImageUrls,
                        "reviews" to reviewCount,
                        "averageRating" to averageRating)
    }

}