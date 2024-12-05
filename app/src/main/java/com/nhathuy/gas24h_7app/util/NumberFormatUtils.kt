package com.nhathuy.gas24h_7app.util

import java.text.NumberFormat
import java.util.Locale

object NumberFormatUtils {
    fun formatDiscount(value:Double):String{
        return when{
            value < 1000 -> String.format("%.0fđ",value)
            value <1000000 -> String.format("%.0fk",value/1000)
            else -> String.format("%.2ftr",value/ 1000000)
        }
    }
    fun formatPrice(value:Double):String{
        val numberFormat = NumberFormat.getInstance(Locale("vi","VN")).apply {
            isGroupingUsed=true
        }
        return when{
            value < 1000 -> String.format("đ%s", numberFormat.format(value))
            value < 1000000 -> String.format("đ%s", numberFormat.format(value))
            else -> String.format("đ%s", numberFormat.format(value))
        }
    }
    fun formatPriceVN(value: Double): String {
        val numberFormat = NumberFormat.getInstance(Locale("vi", "VN")).apply {
            isGroupingUsed = true
        }
        return String.format("%s VNĐ", numberFormat.format(value))
    }

}