package com.nhathuy.gas24h_7app.data.model

import com.google.firebase.firestore.DocumentSnapshot

data class User(
    var uid:String = "",
    var fullName:String = "",
    var phoneNumber: String ="",
    var province:String = "",
    var district:String ="",
    var ward:String ="",
    var houseNumber:String ="",
    var address: String = "",
    var referralCode:String ="",
    var imageUser: String = "",
    var lastOnline: Long = System.currentTimeMillis(),
    val fcmToken: String =""
){
    constructor():this("","","","","","","","","","",System.currentTimeMillis(),"")

    fun toMap():Map<String,Any>{
         return hashMapOf(
             "uid" to uid,
             "fullName" to fullName,
             "phoneNumber" to phoneNumber,
             "province" to province,
             "district" to district,
             "ward" to ward,
             "houseNumber" to houseNumber,
             "address" to address,
             "referralCode" to referralCode,
             "imageUser" to imageUser,
             "lastOnline" to lastOnline,
             "fcmToken" to fcmToken
         )
     }
    companion object{
        fun formDocumentSnapshot(snapshot: DocumentSnapshot):User?{
            return snapshot.toObject(User::class.java)?.apply {
                uid=snapshot.id
            }
        }
    }
}
