package com.nhathuy.gas24h_7app.di.module

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.admin.add_product.AddProductContract
import com.nhathuy.gas24h_7app.admin.add_product.AddProductPresenter
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
    fun provideCountryRepository():CountryRepository{
        return CountryRepository()
    }
    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firebaseAuth, firestore)
    }

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

    @Provides
    @Singleton
    fun provideLoginPresenter(auth: FirebaseAuth, countryRepository: CountryRepository): LoginContract.Presenter {
        return LoginPresenter(auth, countryRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterPresenter(locationApiService: LocationApiService, userRepository: UserRepository,coroutineScope: CoroutineScope,context: Context): RegisterContract.Presenter {
        return RegisterPresenter(locationApiService, userRepository,coroutineScope,context)
    }

    @Provides
    @Singleton
    fun provideAddProductPresenter(context: Context,db:FirebaseFirestore,storage: FirebaseStorage):AddProductContract.Presenter{
        return AddProductPresenter(context,db,storage)
    }

    @Provides
    @Singleton
    fun provideProductListCategory(db:FirebaseFirestore):ProductListCategoryContract.Presenter{
        return ProductListCategoryPresenter(db)
    }
    @Provides
    @Singleton
    fun provideHomeFragment(db:FirebaseFirestore,storage: FirebaseStorage):HomeFragmentContract.Presenter{
        return HomeFragmentPresenter(db,storage)
    }

}