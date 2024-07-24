package com.nhathuy.gas24h_7app

import android.app.Application
import com.nhathuy.gas24h_7app.di.component.DaggerGasComponent
import com.nhathuy.gas24h_7app.di.component.GasComponent
import com.nhathuy.gas24h_7app.di.module.GasModule

class Gas24h_7Application:Application() {

    private lateinit var gasComponent: GasComponent

    override fun onCreate() {
        super.onCreate()
        gasComponent=DaggerGasComponent.builder().gasModule(GasModule(this)).build()
    }
    fun getGasComponent():GasComponent{
        return gasComponent
    }
}