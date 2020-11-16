package com.iat359.biteme

import android.provider.BaseColumns

object RecipeReaderContract {
    // Table contents are grouped together in an anonymous object.
    object RecipeEntry : BaseColumns {
        const val TABLE_NAME = "RecipesTable"
        const val NAME = "name"
        const val IMAGE_NAME = "image_name"
        const val INGREDIENTS = "ingredients"
        const val RECIPE_STEPS = "recipe_steps"
        const val RATING = "rating"
    }
}