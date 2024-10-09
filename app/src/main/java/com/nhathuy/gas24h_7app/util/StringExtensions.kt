package com.nhathuy.gas24h_7app.util

import java.text.Normalizer

fun String.normalizeVietnamese() :String{
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val temp = Normalizer.normalize(this,Normalizer.Form.NFD)
    return regex.replace(temp,"").lowercase()
        .replace('đ', 'd')
        .replace('Đ', 'd')
}