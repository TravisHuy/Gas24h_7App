package com.nhathuy.gas24h_7app.data.model

//enum class ProductCategory(val categoryName:String) {
//    GAS_VIP("Gas VIP Khuyến mãi"),
//    RESTAURANT_GAS("Bộ bình-Gas Quán Ăn"),
//    ACCESSORY("Phụ Kiện"),
//    GAS_STOVE("Bếp Gas"),
//    BUILT_IN_GAS_STOVE("Bếp Gas Âm"),
//    INDUCTION_COOKER("Bếp Từ"),
//    ELECTRIC_COOK_TOP("Bếp Điện"),
//    INFRARED_COOK_TOP("Bếp Hồng Ngoại"),
//    MICROWAVE_OVEN("Lò Nướng-Lò Vi Sóng"),
//    RANGE_HOOD("Máy Hút Mùi"),
//    KITCHEN_SINK("Bồn Rửa"),
//    GAS_STOVE_WITH_OVEN("Bếp Gas Lò Nướng"),
//    FURNITURE("Thiết bị Nội Thất");
//
//    companion object {
//        fun fromDisplayName(categoryName: String): ProductCategory? {
//            return values().find { it.categoryName == categoryName }
//        }
//    }
//}
data class ProductCategory(val id:String,val categoryName:String){

    companion object{
        fun fromMap(map:Map<String,Any>):ProductCategory{
            return ProductCategory(id = map["id"] as String,
                                    categoryName = map["categoryName"] as String)
        }
        fun toMap(category: ProductCategory) : Map<String,Any>{
            return hashMapOf(
                "id" to category.id,
                "categoryName" to category.categoryName
            )
        }
    }
}