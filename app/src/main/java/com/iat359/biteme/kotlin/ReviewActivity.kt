package com.iat359.biteme.kotlin

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import kotlinx.android.synthetic.main.activity_review.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.iat359.biteme.R
import com.iat359.biteme.kotlin.model.Recipe
import com.iat359.biteme.kotlin.model.RecipeFirestore
import com.iat359.biteme.kotlin.model.Review
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_review.reviewImage
import kotlinx.android.synthetic.main.activity_review.recipeName
import java.io.ByteArrayOutputStream
import java.sql.Timestamp

class ReviewActivity : BaseActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var recipeRef: DocumentReference
    private lateinit var recipe_name: String
    private var review_image_name: String? = null
    private var reviewImagePath: String? = null
    private var saved : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get recipe name from extras
        recipe_name = intent.getStringExtra("RECIPE_NAME")
        val imageName = intent.getStringExtra("IMAGE_NAME")

        recipeName.text = recipe_name

        // Initialize Firestore
        firestore = Firebase.firestore

        storage = Firebase.storage
        // Get reference to the restaurant
        recipeRef = firestore.collection("recipes").document(recipe_name)

        camera_button.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val save = findViewById<View>(R.id.save_button)

        Glide.with(this)
                .load(imageName)
                .into(reviewImage)

        delete_photo_button.setOnClickListener{
           deleteImage()
        }

        save.setOnClickListener{
            saved = true
            onSaveClicked()
            finish()
        }

        cancel_button.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // make sure image gets deleted from cloud storage if review wasnt saved
        if(!saved) {
            deleteImage()
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Disable save button and delete photo button until image is uploaded to cloud storage
            save_button.isClickable = false
            delete_photo_button.isClickable = false

            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadedReviewImage.setImageBitmap(imageBitmap)

            // Create a storage reference from our app
            val storageRef = storage.reference

            reviewImagePath = "reviewImages/" + recipe_name + reviewFormName.text.toString() + Timestamp(System.currentTimeMillis()) + ".jpg"
            // Create a reference to review image
            val reviewImageRef = storageRef.child(reviewImagePath!!)

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = reviewImageRef.putBytes(data)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                reviewImageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    review_image_name = task.result.toString()
                } else {
                    // Handle failures
                    // ...
                }

                // re-enable save button/delete photo button no matter what
                save_button.isClickable = true
                delete_photo_button.isClickable = true
            }
        }
    }

    private fun onSaveClicked() {
        val review = Review(
                reviewFormName.text.toString(),
                recipe_name,
                reviewFormText.text.toString(),
                reviewFormRating.rating.toDouble(),
                review_image_name
        )

        onReview(review)

    }

    fun onReview(review: Review) {
        // In a transaction, add the new rating and update the aggregate totals
        addReview(recipeRef, review)
                .addOnSuccessListener(this) {
                    Log.d(TAG, "Rating added")

                    finish()
                }
                .addOnFailureListener(this) { e ->
                    Log.w(TAG, "Add rating failed", e)

                    // Show failure message and hide keyboard
                    hideKeyboard()
                    Snackbar.make(findViewById(android.R.id.content), "Failed to add review",
                            Snackbar.LENGTH_SHORT).show()
                }
    }

    private fun addReview(recipeRef: DocumentReference, review: Review): Task<Void> {
        // Create reference for new rating, for use inside the transaction
        val reviewRef = recipeRef.collection("reviews").document()

        // In a transaction, add the new rating and update the aggregate totals
        return firestore.runTransaction { transaction ->
            val recipe = transaction.get(recipeRef).toObject<RecipeFirestore>()
            if (recipe == null) {
                throw Exception("Recipe not found at ${recipeRef.path}")
            }

            // Compute new number of ratings
            val newNumRatings = recipe.numRatings + 1

            // Compute new average rating
            val oldRatingTotal = recipe.avgRating * recipe.numRatings
            val newAvgRating = (oldRatingTotal + review.rating) / newNumRatings

            // Set new restaurant info
            recipe.numRatings = newNumRatings
            recipe.avgRating = newAvgRating

            // Commit to Firestore
            transaction.set(recipeRef, recipe)
            transaction.set(reviewRef, review)

            null
        }
    }

    private fun deleteImage() {
        // Clear review image and delete image from cloud storage
        uploadedReviewImage.setImageDrawable(null)
        if(reviewImagePath != null ) {
            // Create a storage reference from our app
            val storageRef = storage.reference
            Log.d(TAG, reviewImagePath)
            // Create a reference to the file to delete
            val reviewImageRef = storageRef.child(reviewImagePath!!)

            // Delete the file
            reviewImageRef.delete().addOnSuccessListener {
                // File deleted successfully
                review_image_name = null
                reviewImagePath = null
            }.addOnFailureListener {
                // Uh-oh, an error occurred!
            }
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    companion object {

        const val KEY_RECIPE_ID = "key_recipe_id"

        private const val TAG = "ReviewDialog"
    }
}