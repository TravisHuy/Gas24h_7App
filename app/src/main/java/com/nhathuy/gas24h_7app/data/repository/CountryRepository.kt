package com.nhathuy.gas24h_7app.data.repository

import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.Country
import javax.inject.Inject

class CountryRepository{
    fun getCountries():List<Country>{
        return listOf(
            Country("Vietnam", "+84", R.drawable.ic_flag_vietnam),
            Country("Usa", "+1", R.drawable.ic_flag_usa),
            Country("ThaiLand", "+66", R.drawable.ic_flag_thailan),
            Country("Russia", "+66", R.drawable.ic_flag_russia),
            Country("Japan", "+66", R.drawable.ic_flag_japan),
        )
    }
}