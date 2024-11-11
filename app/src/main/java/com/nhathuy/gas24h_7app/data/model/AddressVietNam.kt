package com.nhathuy.gas24h_7app.data.model

interface AddressVietNam {
    val Id:String
    val Name:String
}
data class Provinces(
    override val Id: String,
    override val Name: String,
    val Districts:List<Districts>
):AddressVietNam
data class Districts(
    override val Id:String,
    override val Name: String,
    val Wards:List<Wards>
):AddressVietNam
data class Wards(
    override val Id: String,
    override val Name: String,
    val Level:String
):AddressVietNam
