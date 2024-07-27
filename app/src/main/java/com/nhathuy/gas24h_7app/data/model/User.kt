package com.nhathuy.gas24h_7app.data.model

import com.google.firebase.firestore.DocumentSnapshot

data class User(
    var uid:String = "",
    var fullName:String = "",
    var province:String = "",
    var district:String ="",
    var ward:String ="",
    var address: String = "",
    var referralCode:String ="",
    var phoneNumber: String ="",
    var imageUser: String = "",
){

     fun toMap():Map<String,Any>{
         return hashMapOf(
             "uid" to uid,
             "fullName" to fullName,
             "province" to province,
             "district" to district,
             "ward" to ward,
             "address" to address,
             "referralCode" to referralCode,
             "phoneNumber" to phoneNumber,
             "imageUser" to imageUser)
     }
    companion object{
        fun formDocumentSnapshot(snapshot: DocumentSnapshot):User?{
            return snapshot.toObject(User::class.java)?.apply {
                uid=snapshot.id
            }
        }
    }
}
