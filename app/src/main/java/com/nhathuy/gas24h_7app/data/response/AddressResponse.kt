package com.nhathuy.gas24h_7app.data.response

import com.nhathuy.gas24h_7app.data.model.Province

data class AddressResponse( val error: Int,
                            val error_text: String,
                            val data_name: String,
                            val data: List<Province>)
