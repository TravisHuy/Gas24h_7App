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
import java.lang.Exception
import javax.inject.Inject

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
}