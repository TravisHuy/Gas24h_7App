package com.nhathuy.gas24h_7app.di.component

import com.nhathuy.gas24h_7app.admin.AdminActivity
import com.nhathuy.gas24h_7app.admin.add_product.AddProductActivity
import com.nhathuy.gas24h_7app.data.api.RetrofitClient
import com.nhathuy.gas24h_7app.di.module.FirebaseModule
import com.nhathuy.gas24h_7app.di.module.GasModule
import com.nhathuy.gas24h_7app.di.module.PresenterModule
import com.nhathuy.gas24h_7app.di.module.RepositoryModule
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryFragment
import com.nhathuy.gas24h_7app.fragment.home.HomeFragment
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.ui.register.RegisterActivity
import com.nhathuy.gas24h_7app.ui.splash.SplashActivity
import com.nhathuy.gas24h_7app.ui.verify.VerificationActivity
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [GasModule::class,RetrofitClient::class,FirebaseModule::class,PresenterModule::class,RepositoryModule::class])
interface GasComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(verifyActivity: VerificationActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(mainActivity: MainActivity)

    fun inject(adminActivity: AdminActivity)
    fun inject(addProductActivity: AddProductActivity)

    fun inject(homeFragment: HomeFragment)
    fun inject(productListCategoryFragment: ProductListCategoryFragment)

    fun inject(detailProductActivity: DetailProductActivity)
}