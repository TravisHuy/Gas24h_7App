package com.nhathuy.gas24h_7app.di.component

import com.nhathuy.gas24h_7app.admin.AdminActivity
import com.nhathuy.gas24h_7app.admin.add_product.AddProductActivity
import com.nhathuy.gas24h_7app.admin.order.pending_confirmation.PendingConfirmationActivity
import com.nhathuy.gas24h_7app.admin.order.shipping.ShippingActivity
import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllProductActivity
import com.nhathuy.gas24h_7app.admin.voucher.detail_product.VoucherDetailProductActivity
import com.nhathuy.gas24h_7app.data.api.RetrofitClient
import com.nhathuy.gas24h_7app.di.module.FirebaseModule
import com.nhathuy.gas24h_7app.di.module.GasModule
import com.nhathuy.gas24h_7app.di.module.PresenterModule
import com.nhathuy.gas24h_7app.di.module.RepositoryModule
import com.nhathuy.gas24h_7app.di.module.ViewModelModule
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryFragment
import com.nhathuy.gas24h_7app.fragment.home.HomeFragment
import com.nhathuy.gas24h_7app.fragment.logout.LogoutFragment
import com.nhathuy.gas24h_7app.fragment.profile.ProfileFragment
import com.nhathuy.gas24h_7app.ui.add_review.AddReviewActivity
import com.nhathuy.gas24h_7app.ui.all_voucher.AllVoucherActivity
import com.nhathuy.gas24h_7app.ui.buy_back.BuyBackActivity
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import com.nhathuy.gas24h_7app.ui.choose_voucher.ChooseVoucherActivity
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.ui.order.OrderActivity
import com.nhathuy.gas24h_7app.ui.order_information.OrderInformationActivity
import com.nhathuy.gas24h_7app.ui.pending_payment.PendingPaymentActivity
import com.nhathuy.gas24h_7app.ui.purchased_order.PurchasedOrderActivity
import com.nhathuy.gas24h_7app.ui.register.RegisterActivity
import com.nhathuy.gas24h_7app.ui.splash.SplashActivity
import com.nhathuy.gas24h_7app.ui.verify.VerificationActivity
import com.nhathuy.gas24h_7app.viewmodel.ViewModelFactory
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [GasModule::class,RetrofitClient::class,FirebaseModule::class,PresenterModule::class,RepositoryModule::class,ViewModelModule::class])
interface GasComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(verifyActivity: VerificationActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(mainActivity: MainActivity)

    fun inject(adminActivity: AdminActivity)
    fun inject(addProductActivity: AddProductActivity)

    fun inject(homeFragment: HomeFragment)
    fun inject(productListCategoryFragment: ProductListCategoryFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(detailProductActivity: DetailProductActivity)
    fun inject(cartActivity: CartActivity)
    fun inject(orderActivity: OrderActivity)
    fun inject(voucherAllProductActivity: VoucherAllProductActivity)
    fun inject(voucherDetailProductActivity: VoucherDetailProductActivity)
    fun inject(chooseVoucherActivity: ChooseVoucherActivity)
    fun viewModelFactory(): ViewModelFactory

    fun inject(paymentActivity: PendingPaymentActivity)
    fun inject(purchasedOrderActivity: PurchasedOrderActivity)
    fun inject(pendingConfirmationActivity: PendingConfirmationActivity)

    fun inject(shippingActivity: ShippingActivity)
    fun inject(orderInformationActivity: OrderInformationActivity)
    fun inject(buyBackActivity: BuyBackActivity)
    fun inject(allVoucherActivity: AllVoucherActivity)
    fun inject(addReviewActivity: AddReviewActivity)
}