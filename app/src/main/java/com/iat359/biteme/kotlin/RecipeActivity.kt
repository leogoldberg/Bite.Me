package com.iat359.biteme.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.iat359.biteme.R
import com.iat359.biteme.databinding.ActivityRecipeBinding
import com.iat359.biteme.kotlin.adapter.ReviewAdapter
import com.iat359.biteme.kotlin.database.RecipeDatabase
import com.iat359.biteme.kotlin.model.Recipe
import com.iat359.biteme.kotlin.model.RecipeFirestore

import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : BaseActivity(), EventListener<DocumentSnapshot> {
    private val db by lazy { RecipeDatabase(this) }
    private lateinit var firestore: FirebaseFirestore
    lateinit var recipe : Recipe
    lateinit var reviewAdapter: ReviewAdapter
    private lateinit var binding: ActivityRecipeBinding
    private lateinit var recipeRef: DocumentReference
    private var recipeRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val name = intent.getStringExtra("EXTRA_NAME")
        val imageName = intent.getStringExtra("EXTRA_IMAGENAME")
        val ingredients = intent.getStringArrayListExtra("EXTRA_INGREDIENTS")
        val recipeSteps = intent.getStringArrayListExtra("EXTRA_STEPS")

        recipeName.text = name

        for(ingredient in ingredients)
        {
            var ingredientView = TextView(this, null, R.style.AppTheme_Body1)
            ingredientView.text = ingredient
            ingredientsLayout.addView(ingredientView)

            val context = ContextThemeWrapper(this, R.style.AppTheme_Divider)
            var divider = View(context)
            ingredientsLayout.addView(divider)
        }

        for(step in recipeSteps)
        {
            var stepView = TextView(this, null, R.style.AppTheme_Body1)
            stepView.text = step
            stepsLayout.addView(stepView)

            val context = ContextThemeWrapper(this, R.style.AppTheme_Divider)
            var divider = View(context)
            stepsLayout.addView(divider)
        }
        Glide.with(this)
                .load(imageName)
                .into(recipeImage)

        // Initialize Firestore
        firestore = Firebase.firestore

        recipeRef = firestore.collection("recipes").document(name)

        val reviewsQuery = recipeRef
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)

        // RecyclerView
        reviewAdapter = object : ReviewAdapter(reviewsQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    binding.recyclerReviews.visibility = View.GONE
                    binding.viewEmptyReviews.visibility = View.VISIBLE
                } else {
                    reviewHeader.text="Reviews ($itemCount)"
                    binding.recyclerReviews.visibility = View.VISIBLE
                    binding.viewEmptyReviews.visibility = View.GONE
                }
            }
        }
        binding.recyclerReviews.layoutManager = LinearLayoutManager(this)
        binding.recyclerReviews.adapter = reviewAdapter


        recipe = Recipe(0, name, imageName, ingredients, recipeSteps)

        recipeButtonBack.setOnClickListener{
            onBackPressed()
        }

        bookmarkButton.setOnClickListener {
            createBookmark()
        }

        reviewButton.setOnClickListener {
            createReview()
        }

    }

    public override fun onStart() {
        super.onStart()

        reviewAdapter.startListening()
        recipeRegistration = recipeRef.addSnapshotListener(this)
    }

    public override fun onStop() {
        super.onStop()

        reviewAdapter.stopListening()

        recipeRegistration?.remove()
        recipeRegistration = null
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "recipe:onEvent", e)
            return
        }

        snapshot?.let {
            val recipe = snapshot.toObject<RecipeFirestore>()
            if (recipe != null) {
                onRecipeLoaded(recipe)
            }
        }
    }

    private fun onRecipeLoaded(recipe: RecipeFirestore) {
        binding.recipeRating.rating = recipe.avgRating.toFloat()
        binding.recipeNumRatings.text = getString(R.string.fmt_num_ratings, recipe.numRatings)
    }

    fun createBookmark() {
        db.insertBookmark(recipe)
        Toast.makeText(this@RecipeActivity, "Recipe Saved!", Toast.LENGTH_SHORT).show()
    }

    fun createReview() {
        Intent(this, ReviewActivity::class.java).also{
            it.putExtra("RECIPE_NAME", recipe.name)
            it.putExtra("IMAGE_NAME", recipe.imageName)
            startActivity(it)
        }
    }

    companion object {

        private const val TAG = "RecipeDetail"
    }
}