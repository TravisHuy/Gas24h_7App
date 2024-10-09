package com.nhathuy.gas24h_7app.admin.product_management.all_product

import com.nhathuy.gas24h_7app.data.model.Product

interface AllProductContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showProducts(products:List<Product>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadProducts()
        fun sortProductsByPrice(highToLow:Boolean)
        fun sortProductsByStock(highToLow:Boolean)
        fun sortProductsBySelling(highToLow:Boolean)
        fun searchProducts(query:String)
    }
}