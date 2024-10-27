package com.nhathuy.gas24h_7app.data.model

data class ProductSalesSummary(
    val productId:String,
    val productName:String,
    val quantitySold:Int,
    val revenue:Double
){
    constructor():this("","",0,0.0)

    fun toMap():Map<String,Any>{
        return hashMapOf(
            "productId" to productId,
            "productName" to productName,
            "quantitySold" to quantitySold,
            "revenue" to revenue
        )
    }
}
