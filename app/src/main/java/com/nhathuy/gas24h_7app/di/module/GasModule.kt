package com.nhathuy.gas24h_7app.di.module

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.ui.login.LoginContract
import com.nhathuy.gas24h_7app.ui.login.LoginPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GasModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideFirebaseAuth():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Provides
    @Singleton
    fun provideFirebaseFireStore():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage():FirebaseStorage{
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun LoginPresenter(auth:FirebaseAuth):LoginContract.Presenter{
        return LoginPresenter(auth)
    }

}