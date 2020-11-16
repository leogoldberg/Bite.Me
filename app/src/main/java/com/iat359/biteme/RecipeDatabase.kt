package com.iat359.biteme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.iat359.biteme.RecipeReaderContract.RecipeEntry;

class RecipeDatabase(context: Context) {
    private val helper = RecipeReaderDbHelper(context);
    private val db = helper.writableDatabase

    fun insertData(name : String, imageName: String, ingredients: String, recipeSteps: String, rating: Float) : Long {
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

    fun getAllData() : MutableList<Recipe> {
        val projection = arrayOf(
                BaseColumns._ID,
                RecipeEntry.NAME,
                RecipeEntry.IMAGE_NAME)

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

        val recipes = mutableListOf<Recipe>()
        with(cursor) {
            while(moveToNext()) {
                val id = getLong(index0)
                val recipeName = getString(index1)
                val recipeImageName = getString(index2)
                val recipe = Recipe(id, recipeName, recipeImageName)
                recipes.add(recipe)
            }
        }

        return recipes
    }

}