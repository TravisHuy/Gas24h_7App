package com.nhathuy.gas24h_7app.data.model

data class RevenueStatistics(
    val totalRevenue:Double,
    val totalOrders:Int,
    val totalProductSold:Int,
    val revenueByPeriod: List<RevenuePeriod>,
    val topSellingProducts: List<ProductSalesSummary>
){
    constructor():this(0.0,0,0, emptyList(), emptyList())

    fun toMap():Map<String,Any>{
        return hashMapOf(
            "totalRevenue"  to totalRevenue,
            "totalOrders" to totalOrders,
            "totalProductSold" to totalProductSold,
            "revenueByPeriod" to revenueByPeriod,
            "topSellingProducts" to topSellingProducts
        )
    }
}
