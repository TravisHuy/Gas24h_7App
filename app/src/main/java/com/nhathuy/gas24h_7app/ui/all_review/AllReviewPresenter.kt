package com.nhathuy.gas24h_7app.ui.all_review

import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

class AllReviewPresenter @Inject constructor(private val userRepository: UserRepository,
                                             private val reviewRepository: ReviewRepository,
                                             private val cartRepository: CartRepository,
):AllReviewContract.Presenter {

    private var view:AllReviewContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: AllReviewContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadReviews(productId:String) {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = reviewRepository.getAllReviewsWithProductId(productId)

                result.fold(
                    onSuccess = {reviews->
                        val userId = reviews.map {
                            it.userId
                        }
                        val userMap = mutableMapOf<String, User>()
                        val userJobs = userId.map {
                                userId ->
                            async(Dispatchers.IO) {
                                val userResult = userRepository.getUser(userId)
                                userResult.getOrNull()?.let {
                                        user -> userMap[userId] = user
                                }
                            }
                        }
                        userJobs.forEach {
                            it.await()
                        }
                        view?.showReviews(reviews,userMap)

                    },
                    onFailure = {
                            e->
                        view?.showMessage("Failed load review ${e.message}")
                    }
                )
            }catch (e:Exception){
                view?.showMessage("Failed load review: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun loadCartItemCount() {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                if(userId!=null){
                    val result = cartRepository.getCartItemCount(userId)
                    result.fold(
                        onSuccess = { count ->
                            view?.updateCartItemCount(count)
                        },
                        onFailure = { e->
                            view?.showMessage(e.message?:"Failed to load cart item count")
                        }
                    )
                }
            }
            catch (e:Exception){
                view?.showMessage(e.message?:"Failed to load cart item count")
            }
        }
    }

    override fun loadReviewsHaveVideoOrImage(productId: String) {
        coroutineScope.launch {
            try {
                val result = reviewRepository.getAllReviewHaveVideoOrImage(productId)
                result.fold(
                    onSuccess = {reviews->
                        val userId = reviews.map {
                            it.userId
                        }
                        val userMap = mutableMapOf<String, User>()
                        val userJobs = userId.map {
                                userId ->
                            async(Dispatchers.IO) {
                                val userResult = userRepository.getUser(userId)
                                userResult.getOrNull()?.let {
                                        user -> userMap[userId] = user
                                }
                            }
                        }
                        userJobs.forEach {
                            it.await()
                        }
                        view?.showReviewsHaveVideoOrImage(reviews,userMap)

                    },
                    onFailure = {
                            e->
                        view?.showMessage("Failed load review ${e.message}")
                    }
                )
            }
            catch (e:Exception){

            }
        }
    }

    override fun loadReviewsByRating(productId: String, rating: Float) {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = reviewRepository.getAllReviewsRating(productId, rating)

                result.fold(
                    onSuccess = { reviews ->
                        val userIds = reviews.map { it.userId }.distinct()
                        val userMap = mutableMapOf<String, User>()
                        val userJobs = userIds.map { userId ->
                            async(Dispatchers.IO) {
                                val userResult = userRepository.getUser(userId)
                                userResult.getOrNull()?.let { user -> userMap[userId] = user }
                            }
                        }
                        userJobs.forEach { it.await() }
                        view?.showFilteredReviews(reviews, userMap)
                    },
                    onFailure = { e ->
                        view?.showMessage("Failed to load filtered reviews: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showMessage("Failed to load filtered reviews: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
}