package com.nhathuy.gas24h_7app.di.component

import com.nhathuy.gas24h_7app.di.module.GasModule
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [GasModule::class])
interface GasComponent {
    fun inject(loginActivity: LoginActivity)
}