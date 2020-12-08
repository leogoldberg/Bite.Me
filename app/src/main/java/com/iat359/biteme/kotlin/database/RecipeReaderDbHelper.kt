package com.iat359.biteme.kotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iat359.biteme.R
import com.iat359.biteme.kotlin.database.RecipeReaderContract.RecipeEntry;
import com.iat359.biteme.kotlin.model.Recipe

private lateinit var firestore: FirebaseFirestore

private const val SQL_CREATE_ENTRIES_RECIPES =
        "CREATE TABLE ${RecipeReaderContract.RECIPE_TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${RecipeEntry.NAME} TEXT," +
                "${RecipeEntry.IMAGE_NAME} TEXT," +
                "${RecipeEntry.INGREDIENTS} TEXT," +
                "${RecipeEntry.RECIPE_STEPS})"

private const val SQL_DELETE_ENTRIES_BOOKMARKS = "DROP TABLE IF EXISTS ${RecipeReaderContract.BOOKMARKS_TABLE_NAME}"

private const val SQL_CREATE_ENTRIES_BOOKMARKS =
        "CREATE TABLE ${RecipeReaderContract.BOOKMARKS_TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${RecipeEntry.NAME} TEXT," +
                "${RecipeEntry.IMAGE_NAME} TEXT," +
                "${RecipeEntry.INGREDIENTS} TEXT," +
                "${RecipeEntry.RECIPE_STEPS})"

private const val SQL_DELETE_ENTRIES_RECIPES = "DROP TABLE IF EXISTS ${RecipeReaderContract.RECIPE_TABLE_NAME}"

class RecipeReaderDbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val context = context
    override fun onCreate(db: SQLiteDatabase) {
        // Initialize Firestore
        firestore = Firebase.firestore

        // SQLlite Setup
        db.execSQL(SQL_CREATE_ENTRIES_RECIPES)
        db.execSQL(SQL_CREATE_ENTRIES_BOOKMARKS)

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
            }

            db.insert(RecipeReaderContract.RECIPE_TABLE_NAME, null, values)
        }

        // FIRESTORE UPLOAD
        if(UPLOAD_FIRESTORE) {
            val recipesRef = firestore.collection("recipes")
            // Add a bunch of random recipes
            for (recipe in recipes) {
                val data = hashMapOf(
                        "numRatings" to 0.0F,
                        "avgRating" to 0.0F
                )
                recipesRef.document(recipe.name).set(data)
            }
        }
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_RECIPES)
        db.execSQL(SQL_DELETE_ENTRIES_BOOKMARKS)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 11
        const val DATABASE_NAME = "RecipeReader.db"
        const val UPLOAD_FIRESTORE = false
        private const val TAG = "DbHelper"
    }
}