package com.nhathuy.gas24h_7app.di.module

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.admin.add_product.AddProductContract
import com.nhathuy.gas24h_7app.admin.add_product.AddProductPresenter
import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllContract
import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllPresenter
import com.nhathuy.gas24h_7app.admin.voucher.detail_product.VoucherDetailContract
import com.nhathuy.gas24h_7app.admin.voucher.detail_product.VoucherDetailPresenter
import com.nhathuy.gas24h_7app.data.api.LocationApiService
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryContract
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryPresenter
import com.nhathuy.gas24h_7app.fragment.home.HomeFragmentContract
import com.nhathuy.gas24h_7app.fragment.home.HomeFragmentPresenter
import com.nhathuy.gas24h_7app.fragment.hotline.HotlineContract
import com.nhathuy.gas24h_7app.ui.cart.CartContract
import com.nhathuy.gas24h_7app.ui.cart.CartPresenter
import com.nhathuy.gas24h_7app.ui.choose_voucher.ChooseVoucherContract
import com.nhathuy.gas24h_7app.ui.choose_voucher.ChooseVoucherPresenter
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductPresenter
import com.nhathuy.gas24h_7app.ui.login.LoginContract
import com.nhathuy.gas24h_7app.ui.login.LoginPresenter
import com.nhathuy.gas24h_7app.ui.order.OrderContract
import com.nhathuy.gas24h_7app.ui.order.OrderPresenter
import com.nhathuy.gas24h_7app.ui.register.RegisterContract
import com.nhathuy.gas24h_7app.ui.register.RegisterPresenter
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideLoginPresenter(auth: FirebaseAuth,db: FirebaseFirestore, countryRepository: CountryRepository): LoginContract.Presenter {
        return LoginPresenter(auth,db, countryRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterPresenter(locationApiService: LocationApiService, userRepository: UserRepository, coroutineScope: CoroutineScope, context: Context): RegisterContract.Presenter {
        return RegisterPresenter(locationApiService, userRepository,coroutineScope,context)
    }

    @Provides
    @Singleton
    fun provideAddProductPresenter(context: Context, db: FirebaseFirestore, storage: FirebaseStorage): AddProductContract.Presenter{
        return AddProductPresenter(context,db,storage)
    }

    @Provides
    @Singleton
    fun provideProductListCategoryPresenter(db: FirebaseFirestore): ProductListCategoryContract.Presenter{
        return ProductListCategoryPresenter(db)
    }
    @Provides
    @Singleton
    fun provideHomeFragmentPresenter(db: FirebaseFirestore, storage: FirebaseStorage): HomeFragmentContract.Presenter{
        return HomeFragmentPresenter(db,storage)
    }

    @Provides
    @Singleton
    fun provideProductDetailPresenter(repository: ProductRepository,cartRepository: CartRepository,userRepository: UserRepository): DetailProductContract.Presenter{
        return DetailProductPresenter(repository,cartRepository,userRepository)
    }

    @Provides
    @Singleton
    fun provideCartPresenter(cartRepository: CartRepository,userRepository: UserRepository,voucherRepository: VoucherRepository):CartContract.Presenter{
        return CartPresenter(cartRepository,userRepository,voucherRepository)
    }

    @Provides
    @Singleton
    fun provideOrderPresenter(userRepository: UserRepository,productRepository: ProductRepository,voucherRepository: VoucherRepository,cartRepository: CartRepository,orderRepository: OrderRepository):OrderContract.Presenter{
        return OrderPresenter(userRepository,productRepository,voucherRepository,cartRepository, orderRepository)
    }
    @Provides
    @Singleton
    fun provideVoucherAllPresenter(voucherRepository: VoucherRepository):VoucherAllContract.Presenter{
        return VoucherAllPresenter(voucherRepository)
    }

    @Provides
    @Singleton
    fun provideVoucherDetailPresenter(voucherRepository: VoucherRepository,productRepository: ProductRepository):VoucherDetailContract.Presenter{
        return VoucherDetailPresenter(voucherRepository,productRepository)
    }

    @Provides
    @Singleton
    fun provideChooseVoucherPresenter(voucherRepository: VoucherRepository,context: Context):ChooseVoucherContract.Presenter{
        return ChooseVoucherPresenter(voucherRepository,context)
    }
}