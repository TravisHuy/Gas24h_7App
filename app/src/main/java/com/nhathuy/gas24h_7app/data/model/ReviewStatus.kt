package com.nhathuy.gas24h_7app.data.model

enum class ReviewStatus(val displayName:String) {
    ONE_STAR("Tệ"),
    TWO_STARS("Kém"),
    THREE_STARS("Bình thường"),
    FOUR_STARS("Tốt"),
    FIVE_STARS("Xuất sắc");

    companion object {
        fun fromStars(stars: Int): ReviewStatus {
            return when (stars) {
                1 -> ONE_STAR
                2 -> TWO_STARS
                3 -> THREE_STARS
                4 -> FOUR_STARS
                5 -> FIVE_STARS
                else -> throw IllegalArgumentException("Invalid number of stars: $stars")
            }
        }
    }
}