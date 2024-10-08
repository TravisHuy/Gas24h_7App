package com.nhathuy.gas24h_7app.di.module

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.admin.product_management.add_product.AddProductContract
import com.nhathuy.gas24h_7app.admin.product_management.add_product.AddProductPresenter
import com.nhathuy.gas24h_7app.data.api.LocationApiService
import com.nhathuy.gas24h_7app.data.api.RetrofitClient
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.impl.UserRepositoryImpl
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryContract
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryPresenter
import com.nhathuy.gas24h_7app.fragment.home.HomeFragment
import com.nhathuy.gas24h_7app.fragment.home.HomeFragmentContract
import com.nhathuy.gas24h_7app.fragment.home.HomeFragmentPresenter
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductPresenter
import com.nhathuy.gas24h_7app.ui.login.LoginContract
import com.nhathuy.gas24h_7app.ui.login.LoginPresenter
import com.nhathuy.gas24h_7app.ui.register.RegisterContract
import com.nhathuy.gas24h_7app.ui.register.RegisterPresenter
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
class GasModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main)
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext
    }

}