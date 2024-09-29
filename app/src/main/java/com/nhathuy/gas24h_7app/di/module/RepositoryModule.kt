package com.nhathuy.gas24h_7app.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.CategoryRepository
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.data.repository.impl.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideCountryRepository(): CountryRepository {
        return CountryRepository()
    }
    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideProductRepository(db:FirebaseFirestore):ProductRepository{
        return ProductRepository(db)
    }

    @Provides
    @Singleton
    fun provideCartRepository(db:FirebaseFirestore):CartRepository{
        return CartRepository(db)
    }
    @Provides
    @Singleton
    fun provideVoucherRepository(db:FirebaseFirestore): VoucherRepository {
        return VoucherRepository(db)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(db:FirebaseFirestore,voucherRepository: VoucherRepository): OrderRepository {
        return OrderRepository(db,voucherRepository)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(db:FirebaseFirestore): CategoryRepository {
        return CategoryRepository(db)
    }
    @Provides
    @Singleton
    fun provideReviewRepository(db:FirebaseFirestore,storage:FirebaseStorage): ReviewRepository {
        return ReviewRepository(db, storage)
    }
}