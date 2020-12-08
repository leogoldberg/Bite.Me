package com.iat359.biteme.kotlin.database

import android.provider.BaseColumns

object RecipeReaderContract {
    const val RECIPE_TABLE_NAME = "RecipesTable"
    const val BOOKMARKS_TABLE_NAME = "BookmarksTable"
    // Table contents are grouped together in an anonymous object.
    object RecipeEntry : BaseColumns {
        const val NAME = "name"
        const val IMAGE_NAME = "image_name"
        const val INGREDIENTS = "ingredients"
        const val RECIPE_STEPS = "recipe_steps"
    }
}