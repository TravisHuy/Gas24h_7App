package com.nhathuy.gas24h_7app.di.module

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.admin.product_management.add_product.AddProductContract
import com.nhathuy.gas24h_7app.admin.product_management.add_product.AddProductPresenter
import com.nhathuy.gas24h_7app.admin.order.pending_confirmation.PendingConfirmationContract
import com.nhathuy.gas24h_7app.admin.order.pending_confirmation.PendingConfirmationPresenter
import com.nhathuy.gas24h_7app.admin.order.shipping.ShippingContract
import com.nhathuy.gas24h_7app.admin.order.shipping.ShippingPresenter
import com.nhathuy.gas24h_7app.admin.product_management.all_product.AllProductPresenter
import com.nhathuy.gas24h_7app.admin.product_management.edit_product.EditProductPresenter
import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllContract
import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllPresenter
import com.nhathuy.gas24h_7app.admin.voucher.detail_product.VoucherDetailContract
import com.nhathuy.gas24h_7app.admin.voucher.detail_product.VoucherDetailPresenter
import com.nhathuy.gas24h_7app.data.api.LocationApiService
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.CategoryRepository
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryContract
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryPresenter
import com.nhathuy.gas24h_7app.fragment.home.HomeFragmentContract
import com.nhathuy.gas24h_7app.fragment.home.HomeFragmentPresenter
import com.nhathuy.gas24h_7app.fragment.profile.ProfileContract
import com.nhathuy.gas24h_7app.fragment.profile.ProfilePresenter
import com.nhathuy.gas24h_7app.ui.add_review.AddReviewContract
import com.nhathuy.gas24h_7app.ui.add_review.AddReviewPresenter
import com.nhathuy.gas24h_7app.ui.add_review_test.AddReviewTestContract
import com.nhathuy.gas24h_7app.ui.add_review_test.AddReviewTestPresenter
import com.nhathuy.gas24h_7app.ui.all_review.AllReviewPresenter
import com.nhathuy.gas24h_7app.ui.all_voucher.AllVoucherContract
import com.nhathuy.gas24h_7app.ui.all_voucher.AllVoucherPresenter
import com.nhathuy.gas24h_7app.ui.buy_back.BuyBackContract
import com.nhathuy.gas24h_7app.ui.buy_back.BuyBackPresenter
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
import com.nhathuy.gas24h_7app.ui.order_information.OrderInformationContract
import com.nhathuy.gas24h_7app.ui.order_information.OrderInformationPresenter
import com.nhathuy.gas24h_7app.ui.pending_payment.PendingPaymentContract
import com.nhathuy.gas24h_7app.ui.pending_payment.PendingPaymentPresenter
import com.nhathuy.gas24h_7app.ui.purchased_order.PurchasedOrderContract
import com.nhathuy.gas24h_7app.ui.purchased_order.PurchasedOrderPresenter
import com.nhathuy.gas24h_7app.ui.register.RegisterContract
import com.nhathuy.gas24h_7app.ui.register.RegisterPresenter
import com.nhathuy.gas24h_7app.ui.review_of_me.ReviewOfMePresenter
import com.nhathuy.gas24h_7app.ui.verify.VerificationContract
import com.nhathuy.gas24h_7app.ui.verify.VerificationPresenter
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
    fun provideVerifyPresenter(auth: FirebaseAuth,userRepository: UserRepository): VerificationContract.Presenter {
        return VerificationPresenter(auth,userRepository)
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
    fun provideEditProductPresenter(context: Context, db: FirebaseFirestore, storage: FirebaseStorage,productRepository: ProductRepository): EditProductPresenter{
        return EditProductPresenter(context,db,storage,productRepository)
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
    fun provideProductDetailPresenter(repository: ProductRepository,cartRepository: CartRepository,userRepository: UserRepository,reviewRepository: ReviewRepository): DetailProductContract.Presenter{
        return DetailProductPresenter(repository,cartRepository,userRepository,reviewRepository)
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

    @Provides
    @Singleton
    fun providePendingPaymentPresenter(repository: ProductRepository,cartRepository: CartRepository,userRepository: UserRepository): PendingPaymentContract.Presenter{
        return PendingPaymentPresenter(repository,cartRepository,userRepository)
    }

    @Provides
    @Singleton
    fun providePurchasedOrderPresenter(orderRepository: OrderRepository,productRepository: ProductRepository,userRepository: UserRepository):PurchasedOrderContract.Presenter{
        return PurchasedOrderPresenter(orderRepository,productRepository,userRepository)
    }

    @Provides
    @Singleton
    fun providePendingConfirmationOrderPresenter(orderRepository: OrderRepository,productRepository: ProductRepository,userRepository: UserRepository):PendingConfirmationContract.Presenter{
        return PendingConfirmationPresenter(orderRepository,productRepository,userRepository)
    }

    @Provides
    @Singleton
    fun provideShippingOrderPresenter(orderRepository: OrderRepository,productRepository: ProductRepository,userRepository: UserRepository):ShippingContract.Presenter{
        return ShippingPresenter(orderRepository,productRepository,userRepository)
    }

    @Provides
    @Singleton
    fun provideProfilePresenter(firebaseStorage: FirebaseStorage,userRepository: UserRepository,orderRepository: OrderRepository,productRepository: ProductRepository,cartRepository: CartRepository):ProfileContract.Presenter{
        return ProfilePresenter(firebaseStorage,userRepository,orderRepository,productRepository,cartRepository)
    }

    @Provides
    @Singleton
    fun provideOrderInformationPresenter(userRepository: UserRepository,orderRepository: OrderRepository,productRepository: ProductRepository):OrderInformationContract.Presenter{
        return OrderInformationPresenter(userRepository,orderRepository,productRepository)
    }
    @Provides
    @Singleton
    fun provideBuyBackPresenter(orderRepository: OrderRepository,userRepository: UserRepository,productRepository: ProductRepository,cartRepository: CartRepository):BuyBackContract.Presenter{
        return BuyBackPresenter(orderRepository,userRepository,productRepository,cartRepository)
    }

    @Provides
    @Singleton
    fun provideAllVoucherPresenter(voucherRepository: VoucherRepository):AllVoucherContract.Presenter{
        return AllVoucherPresenter(voucherRepository)
    }
    @Provides
    @Singleton
    fun provideAddReviewPresenter(reviewRepository: ReviewRepository,userRepository: UserRepository,productRepository: ProductRepository,orderRepository: OrderRepository):AddReviewContract.Presenter{
        return AddReviewPresenter(reviewRepository,userRepository, productRepository, orderRepository)
    }

    @Provides
    @Singleton
    fun provideAddReviewTestPresenter(reviewRepository: ReviewRepository,userRepository: UserRepository,productRepository: ProductRepository,orderRepository: OrderRepository): AddReviewTestContract.Presenter{
        return AddReviewTestPresenter(reviewRepository,userRepository, productRepository, orderRepository)
    }
    @Provides
    @Singleton
    fun provideAllReviewPresenter(userRepository: UserRepository,reviewRepository: ReviewRepository, cartRepository: CartRepository):AllReviewPresenter{
        return AllReviewPresenter(userRepository,reviewRepository, cartRepository)
    }

    @Provides
    @Singleton
    fun provideReviewOfMePresenter(userRepository: UserRepository,reviewRepository: ReviewRepository, productRepository: ProductRepository, orderRepository: OrderRepository):ReviewOfMePresenter{
        return ReviewOfMePresenter(userRepository, reviewRepository, productRepository, orderRepository)
    }

    @Provides
    @Singleton
    fun provideAllProductPresenter(productRepository: ProductRepository,categoryRepository: CategoryRepository):AllProductPresenter{
        return AllProductPresenter(productRepository,categoryRepository)
    }
}