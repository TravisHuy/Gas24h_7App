package com.nhathuy.gas24h_7app.util

object NumberFormatUtils {
    fun formatDiscount(value:Double):String{
        return when{
            value < 1000 -> String.format("%.0fđ",value)
            value <1000000 -> String.format("%.0fk",value/1000)
            else -> String.format("%.2ftr",value/ 1000000)
        }
    }
}