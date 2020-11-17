package com.iat359.biteme

import android.content.ContentValues
import android.content.Context
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

        val newRowId = db.insert(RecipeReaderContract.RECIPE_TABLE_NAME, null, values)

        return newRowId;
    }

    fun getSelectedRecipeData(searchableId : String) : MutableList<Recipe> {
        val projection = arrayOf(
                BaseColumns._ID,
                RecipeEntry.NAME,
                RecipeEntry.IMAGE_NAME,
                RecipeEntry.INGREDIENTS,
                RecipeEntry.RECIPE_STEPS,
                RecipeEntry.RATING
        )

        val cursor = db.query(
                RecipeReaderContract.RECIPE_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )
        val selection = BaseColumns._ID + "='" + searchableId + "'"

        val selectRecipes = mutableListOf<Recipe>()
        with(cursor) {
            while (moveToNext()) {
                val index0 = cursor.getColumnIndex(BaseColumns._ID)
                val index1 = cursor.getColumnIndex(RecipeEntry.NAME)
                val index2 = cursor.getColumnIndex(RecipeEntry.IMAGE_NAME)
                val index3 = cursor.getColumnIndex(RecipeEntry.INGREDIENTS)
                val index4 = cursor.getColumnIndex(RecipeEntry.RECIPE_STEPS)
                val index5 = cursor.getColumnIndex(RecipeEntry.RATING)

                val id = getLong(index0)
                val recipeName = getString(index1)
                val recipeImageName = getString(index2)
                val recipeIngredients = getString(index3).split(".,")
                val recipeSteps = getString(index4).split(".,")
                val rating = getFloat(index5)
                val recipe = Recipe(id, recipeName, recipeImageName, recipeIngredients, recipeSteps, rating)
                selectRecipes.add(recipe)
            }
        }
        return selectRecipes

    }

    fun getAllData(tableName : String) : MutableList<Recipe> {
        val projection = arrayOf(
                BaseColumns._ID,
                RecipeEntry.NAME,
                RecipeEntry.IMAGE_NAME,
                RecipeEntry.INGREDIENTS,
                RecipeEntry.RECIPE_STEPS,
                RecipeEntry.RATING
        )

        val cursor = db.query(
                tableName,
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

    fun insertBookmark(recipe : Recipe) {
        val cursor = db.rawQuery("SELECT name FROM " + RecipeReaderContract.BOOKMARKS_TABLE_NAME + " WHERE name='"+ recipe.name +"'", null)
        if(cursor.count > 0) {
            cursor.close()
            return
        }
        cursor.close()
        val ingredients = recipe.ingredients.joinToString(".,")
        val recipeSteps = recipe.recipeSteps.joinToString(".,")
        val values = ContentValues().apply {
            put(RecipeEntry.NAME, recipe.name)
            put(RecipeEntry.IMAGE_NAME, recipe.imageName)
            put(RecipeEntry.INGREDIENTS, ingredients)
            put(RecipeEntry.RECIPE_STEPS, recipeSteps)
            put(RecipeEntry.RATING, recipe.rating)
        }

        db.insert(RecipeReaderContract.BOOKMARKS_TABLE_NAME, null, values)
    }

    fun deleteBookmark(name : String) {
        db.execSQL("delete from " + RecipeReaderContract.BOOKMARKS_TABLE_NAME + " where name='" + name + "'")
    }

}