package com.nhathuy.gas24h_7app.test_add_review

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.ui.add_review_test.AddReviewTestContract
import com.nhathuy.gas24h_7app.ui.add_review_test.AddReviewTestPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.argThat
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date
import java.util.UUID


@ExperimentalCoroutinesApi
class AddReviewPresenterTest {

    // buộc thực thi đồng bộ trên cùng một luồng (thread)
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // mock là tạo một bản sao
    @Mock
    private lateinit var mockReviewRepository : ReviewRepository
    @Mock
    private lateinit var mockUserRepository: UserRepository
    @Mock
    private lateinit var mockProductRepository: ProductRepository
    @Mock
    private lateinit var mockOrderRepository:OrderRepository
    @Mock
    private lateinit var mockView:AddReviewTestContract.View

    private lateinit var presenter  : AddReviewTestPresenter

    @Before
    fun setUp(){
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        presenter = AddReviewTestPresenter(mockReviewRepository,mockUserRepository,mockProductRepository,mockOrderRepository)

        presenter.attachView(mockView)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        presenter.detachView()
    }


    @Test
    fun `test image addition within max limit`() {
        val mockUri = mock(Uri::class.java)

        presenter.onImageAdded(mockUri)

        verify(mockView).addImageToAdapter(mockUri.toString())
        verify(mockView).updateImageCount(1, 3)
        verify(mockView).enableImageAddButton(true)
    }

    @Test
    fun `test video addition within max limit`() {
        val mockUri = mock(Uri::class.java)
        presenter.onVideoAdded(mockUri)

        verify(mockView).updateVideoCount(1, 1)
    }

    @Test
    fun `test image addition beyond max limit`(){
        val mockUri = mock(Uri::class.java)
        repeat(3){
            presenter.onImageAdded(mockUri)
        }
        presenter.onImageAdded(mockUri)

        verify(mockView).showMessage("Maximum number of images reached")
    }

    @Test
    fun `test video addition`() {
        val mockVideoUri = mock(Uri::class.java)

        presenter.onVideoAdded(mockVideoUri)

        verify(mockView).onVideoAdded(mockVideoUri)
        verify(mockView).enableCoverImageAddButton(false)
        verify(mockView).updateVideoCount(1, 1)
    }

    // this function test error
    @Test
    fun `test review submission with valid data`() = runTest {
        val productId = "product123"
        val orderId = "order456"
        val userId = "user456"
        val rating = 4.5f
        val comment = "Great product!"
        val imageUris = emptyList<Uri>()
        val videoUri = null

        `when`(mockUserRepository.getCurrentUserId()).thenReturn(userId)


        `when`(mockReviewRepository.createReviewTest(
            argThat { review ->
                review != null &&
                review.productId == productId &&
                        review.userId == userId &&
                        review.rating == rating &&
                        review.comment == comment
            },
            eq(imageUris),
            eq(videoUri)
        )).thenReturn(Result.success(Unit))

        `when`(mockOrderRepository.updateOrderStatus(orderId, OrderStatus.RATED))
            .thenReturn(Result.success(Unit))
        `when`(mockProductRepository.updateProductReviewWithTransaction(productId, any()))
            .thenReturn(Result.success(Unit))


//        presenter.orderId = orderId
//        presenter.productId = productId

        // Call the method under test
        presenter.submitReview(rating, comment)

        verify(mockView).showLoading()
        verify(mockReviewRepository).createReviewTest(
            argThat {review ->
                review.userId == userId &&
                        review.rating == rating &&
                        review.comment == comment &&
                        review.productId == productId
            },
            eq(imageUris),
            eq(videoUri)
        )
        verify(mockOrderRepository).updateOrderStatus(eq(orderId), eq(OrderStatus.RATED))
        verify(mockProductRepository).updateProductReviewWithTransaction(eq(productId), any())
        verify(mockView).showMessage("Review submitted successfully")
        verify(mockView).navigateBack()
        verify(mockView).hideLoading()
    }


}