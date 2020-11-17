package com.iat359.biteme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe.*
import java.util.ArrayList

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val name = intent.getStringExtra("EXTRA_NAME")
        val imageName = intent.getStringExtra("EXTRA_IMAGENAME")
        val ingredients = intent.getStringArrayListExtra("EXTRA_INGREDIENTS")
        val recipeSteps = intent.getStringArrayListExtra("EXTRA_STEPS")
        val rating = intent.getFloatExtra("EXTRA_RATING", 0.0f)

        tvRecipeName.text = name
        tvIngredients.text = ingredients?.joinToString { System.lineSeparator() }
        tvRecipe.text = recipeSteps?.joinToString { System.lineSeparator() }
//        val drawableResourceId = this.resources.getIdentifier(imageName, "drawable")
//        recipeImage.setImageResource(drawableResourceId)
        tvRating.text = rating.toString()
    }
}