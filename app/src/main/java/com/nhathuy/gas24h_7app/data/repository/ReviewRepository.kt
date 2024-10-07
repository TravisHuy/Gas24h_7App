package com.nhathuy.gas24h_7app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ReviewRepository @Inject constructor(private val db:FirebaseFirestore,
                                           private val storage: FirebaseStorage
){
    suspend fun createReview(review: Review): Result<Unit> = withContext(Dispatchers.IO){
        try {
            val imageUrls = uploadImages(review.images.map { Uri.parse(it) })
            val videoUrl = review.video.takeIf { it.isNotEmpty() }?.let { uploadVideo(Uri.parse(it)) }

            val updatedReview = review.copy(
                images = imageUrls,
                video = videoUrl ?: ""
            )
            db.collection("reviews").document(updatedReview.id).set(updatedReview.toMap()).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun createReviewTest(review: Review, imageUris:List<Uri>, videoUri:Uri?): Result<Unit>{
        return try {
            val imageUrls = uploadImages(imageUris)
            val videoUrl = videoUri?.let { uploadVideo(it) }
            val updatedReview = review.copy(
                images = imageUrls,
                video = videoUrl ?: ""
            )
            db.collection("reviews").document(updatedReview.id).set(updatedReview.toMap()).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    private suspend fun uploadVideo(uri: Uri):String {
        return uploadMedia(uri,"videos_review/${UUID.randomUUID()}")
    }

    private suspend fun uploadImages(imageUris: List<Uri>): List<String> {
        return imageUris.map {
            uri ->
            uploadMedia(uri,"images_review/${UUID.randomUUID()}")
        }
    }

    private suspend fun uploadMedia(uri: Uri, path: String):String {
        val ref = storage.reference.child(path)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    //get all review
    suspend fun getAllReviews(): Result<List<Review>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("reviews").get().await()
                val reviews = querySnapshot.documents.mapNotNull {
                    it.toObject(Review::class.java)
                }
                Result.success(reviews)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    // get all review with productId
    suspend fun getAllReviewsUserId(userId:String) : Result<List<Review>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("reviews")
                    .whereEqualTo("userId" , userId)
                    .get().await()

                val reviews = querySnapshot.documents.mapNotNull {
                    it.toObject(Review::class.java)
                }

                Result.success(reviews)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    // get all review with productId
    suspend fun getAllReviewsWithProductId(productId:String) : Result<List<Review>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("reviews")
                    .whereEqualTo("productId" , productId)
                    .get().await()

                val reviews = querySnapshot.documents.mapNotNull {
                    it.toObject(Review::class.java)
                }

                Result.success(reviews)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    //get all review with productId and rating
    suspend fun getAllReviewsRating(productId: String,rating:Float) :Result<List<Review>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("reviews")
                    .whereEqualTo("productId" , productId)
                    .whereEqualTo("rating", rating)
                    .get().await()

                val reviews = querySnapshot.documents.mapNotNull {
                    it.toObject(Review::class.java)
                }

                Result.success(reviews)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    //get all review with productId have image and video
    suspend fun getAllReviewHaveVideoOrImage(productId: String) : Result<List<Review>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("reviews")
                    .whereEqualTo("productId" , productId)
                    .get().await()

                val reviews = querySnapshot.documents.mapNotNull { document->
                   val review = document.toObject(Review::class.java)
                   review?.takeIf {
                       (it.video.isNotEmpty() || it.images.isNotEmpty())
                   }
                }

                Result.success(reviews)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
}