package com.iat359.biteme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CardStackAdapter(
        private var recipes: List<Recipe> = emptyList(),
        private val context: Context
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.name.text = "${recipe.id}. ${recipe.name}"

        val drawableResourceId = context.resources.getIdentifier(recipe.imageName, "drawable", context.packageName)

        Glide.with(holder.image)
                .load(drawableResourceId)
                .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, recipe.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
    }

    fun getRecipes(): List<Recipe> {
        return recipes
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.recipe_name)
        var image: ImageView = view.findViewById(R.id.item_image)
    }
}
