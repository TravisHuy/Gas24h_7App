package com.nhathuy.gas24h_7app.ui.buy_back

interface BuyBackContract {
    interface View{

    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
    }
}