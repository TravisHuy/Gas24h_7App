package com.nhathuy.gas24h_7app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.nhathuy.gas24h_7app.di.component.DaggerGasComponent
import com.nhathuy.gas24h_7app.di.component.GasComponent
import com.nhathuy.gas24h_7app.di.module.GasModule

class Gas24h_7Application:Application() {

    private lateinit var gasComponent: GasComponent

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        gasComponent=DaggerGasComponent.builder().gasModule(GasModule(this)).build()
    }
    fun getGasComponent():GasComponent{
        return gasComponent
    }
}