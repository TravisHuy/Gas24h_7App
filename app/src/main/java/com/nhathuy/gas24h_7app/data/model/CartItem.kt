package com.nhathuy.gas24h_7app.data.model

import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    val productId:String,
    var quantity:Int,
    var price:Double,
): Parcelable{
    constructor():this("",0,0.0)
    constructor(parcel: Parcel):this(parcel.readString()?:"",parcel.readInt(),parcel.readDouble())
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeInt(quantity)
        parcel.writeDouble(price)
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}