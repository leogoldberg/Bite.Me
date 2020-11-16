package com.iat359.biteme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SwipeViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val recipes: MutableLiveData<List<Recipe>> by lazy {
        MutableLiveData<List<Recipe>>().also {
            loadRecipes()
        }
    }

    fun getRecipes(): LiveData<List<Recipe>> {
        return recipes
    }

    private fun loadRecipes() : List<Recipe> {
        val db = RecipeDatabase(context)
        return db.getAllRecipeData()
    }
}