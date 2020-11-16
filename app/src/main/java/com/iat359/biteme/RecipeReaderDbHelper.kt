package com.iat359.biteme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iat359.biteme.RecipeReaderContract.RecipeEntry;
import java.io.IOException
import java.io.InputStream

private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${RecipeEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${RecipeEntry.NAME} TEXT," +
                "${RecipeEntry.IMAGE_NAME} TEXT," +
                "${RecipeEntry.INGREDIENTS} TEXT," +
                "${RecipeEntry.RECIPE_STEPS} TEXT," +
                "${RecipeEntry.RATING} REAL)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${RecipeEntry.TABLE_NAME}"

class RecipeReaderDbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val context = context
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)

        val jsonFileString = context.resources.openRawResource(R.raw.recipe_data).bufferedReader().use { it.readText() }

        val gson = Gson()
        val listRecipesType = object : TypeToken<List<Recipe>>() {}.type

        var recipes : List<Recipe> = gson.fromJson(jsonFileString, listRecipesType)

        for (recipe in recipes) {
            val ingredients = recipe.ingredients.joinToString(".,")
            val recipeSteps = recipe.recipeSteps.joinToString(".,")
            val values = ContentValues().apply {
                put(RecipeEntry.NAME, recipe.name)
                put(RecipeEntry.IMAGE_NAME, recipe.imageName)
                put(RecipeEntry.INGREDIENTS, ingredients)
                put(RecipeEntry.RECIPE_STEPS, recipeSteps)
                put(RecipeEntry.RATING, recipe.rating)
            }

            val newRowId = db.insert(RecipeReaderContract.RecipeEntry.TABLE_NAME, null, values)
        }

    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 7
        const val DATABASE_NAME = "RecipeReader.db"
    }
}