package com.iat359.biteme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
            recipeListAdapter = RecipeListAdapter()
            adapter = recipeListAdapter
        }
    }

    private fun addDataSet() {
        val data = db.getAllData(RecipeReaderContract.RECIPE_TABLE_NAME)
        recipeListAdapter.submitList(data)
    }
}