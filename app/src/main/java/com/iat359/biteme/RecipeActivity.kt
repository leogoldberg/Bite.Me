package com.iat359.biteme

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_recipe.*
import java.util.ArrayList

class RecipeActivity : BaseActivity() {
    private val db by lazy { RecipeDatabase(this) }
    lateinit var recipe : Recipe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val name = intent.getStringExtra("EXTRA_NAME")
        val imageName = intent.getStringExtra("EXTRA_IMAGENAME")
        val ingredients = intent.getStringArrayListExtra("EXTRA_INGREDIENTS")
        val recipeSteps = intent.getStringArrayListExtra("EXTRA_STEPS")
        val rating = intent.getFloatExtra("EXTRA_RATING", 0.0f)

        tvRecipeName.text = name
        tvIngredients.text = ingredients?.joinToString("\n")
        tvRecipe.text = recipeSteps?.joinToString("\n")

        val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
        Glide.with(this)
                .load(imageResId)
                .into(recipeImage)

        tvRating.text = rating.toString()

        recipe = Recipe(0, name, imageName, ingredients, recipeSteps, rating)

        bookmarkButton.setOnClickListener {
            createBookmark()
        }

        reviewButton.setOnClickListener {
            createReview()
        }
    }

    fun createBookmark() {
        db.insertBookmark(recipe)
        Toast.makeText(this@RecipeActivity, "Recipe Saved!", Toast.LENGTH_SHORT).show()
    }

    fun createReview() {
        Intent(this, ReviewActivity::class.java).also{
            it.putExtra("RECIPE_NAME", recipe.name)
            startActivity(it)
        }
    }
}