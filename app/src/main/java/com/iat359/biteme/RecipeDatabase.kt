package com.iat359.biteme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.iat359.biteme.RecipeReaderContract.RecipeEntry;

class RecipeDatabase(context: Context) {
    private val helper = RecipeReaderDbHelper(context);
    private val db = helper.writableDatabase

    fun insertRecipeData(name : String, imageName: String, ingredients: String, recipeSteps: String, rating: Float) : Long {
        val values = ContentValues().apply {
            put(RecipeEntry.NAME, name)
            put(RecipeEntry.IMAGE_NAME, imageName)
            put(RecipeEntry.INGREDIENTS, ingredients)
            put(RecipeEntry.RECIPE_STEPS, recipeSteps)
            put(RecipeEntry.RATING, rating)
        }

        val newRowId = db.insert(RecipeReaderContract.RecipeEntry.TABLE_NAME, null, values)

        return newRowId;
    }

    fun getAllRecipeData() : MutableList<Recipe> {
        val projection = arrayOf(
                BaseColumns._ID,
                RecipeEntry.NAME,
                RecipeEntry.IMAGE_NAME,
                RecipeEntry.INGREDIENTS,
                RecipeEntry.RECIPE_STEPS,
                RecipeEntry.RATING
        )

        val cursor = db.query(
                RecipeEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )

        val index0 = cursor.getColumnIndex(BaseColumns._ID)
        val index1 = cursor.getColumnIndex(RecipeEntry.NAME)
        val index2 = cursor.getColumnIndex(RecipeEntry.IMAGE_NAME)
        val index3 = cursor.getColumnIndex(RecipeEntry.INGREDIENTS)
        val index4 = cursor.getColumnIndex(RecipeEntry.RECIPE_STEPS)
        val index5 = cursor.getColumnIndex(RecipeEntry.RATING)

        val recipes = mutableListOf<Recipe>()
        with(cursor) {
            while(moveToNext()) {
                val id = getLong(index0)
                val recipeName = getString(index1)
                val recipeImageName = getString(index2)
                val recipeIngredients = getString(index3).split(".,")
                val recipeSteps = getString(index4).split(".,")
                val rating = getFloat(index5)
                val recipe = Recipe(id, recipeName, recipeImageName, recipeIngredients, recipeSteps, rating)
                recipes.add(recipe)
            }
        }

        return recipes
    }

}