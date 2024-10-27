package com.nhathuy.gas24h_7app.data.model

import java.util.Date

data class RevenuePeriod(
    val period:String,
    val revenue:Double,
    val orderCount:Int,
    val productCount:Int,
    val date:Date
){
    constructor():this("",0.0,0,0,Date())

    fun toMap():Map<String,Any>{
        return hashMapOf(
            "period" to period,
            "revenue" to revenue,
            "orderCount" to orderCount,
            "productCount" to productCount,
            "date" to date
        )
    }
}
