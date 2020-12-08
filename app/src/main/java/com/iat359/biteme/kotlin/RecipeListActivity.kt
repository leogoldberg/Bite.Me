package com.iat359.biteme.kotlin

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.iat359.biteme.R
import com.iat359.biteme.kotlin.adapter.RecipeListAdapter
import com.iat359.biteme.kotlin.database.RecipeDatabase
import com.iat359.biteme.kotlin.database.RecipeReaderContract
import kotlinx.android.synthetic.main.activity_recipe_list.*

class RecipeListActivity : BaseActivity() {
    private val db by lazy { RecipeDatabase(this) }
    private lateinit var recipeListAdapter: RecipeListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        initRecyclerView()
        addDataSet()
    }

    private fun initRecyclerView() {
        recipe_list_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@RecipeListActivity)
            recipeListAdapter = RecipeListAdapter(this@RecipeListActivity)
            adapter = recipeListAdapter
        }
    }

    private fun addDataSet() {
        val data = db.getAllData(RecipeReaderContract.BOOKMARKS_TABLE_NAME)
        recipeListAdapter.submitList(data)
    }
}