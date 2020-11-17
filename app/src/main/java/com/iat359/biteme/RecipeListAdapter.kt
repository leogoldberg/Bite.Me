package com.iat359.biteme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_recipe_list_item.view.*

class RecipeListAdapter (context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var items: MutableList<Recipe> = ArrayList()
    private val db by lazy { RecipeDatabase(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecipeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_recipe_list_item, parent, false), this
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is RecipeViewHolder -> {
                holder.bind(items.get(position))
            }

        }
    }

    fun removeAt(position: Int) {
        val name = items.get(position).name
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
        db.deleteBookmark(name)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(recipeList: MutableList<Recipe>){
        items = recipeList
    }

    class RecipeViewHolder
    constructor(
            itemView: View,
            adapter: RecipeListAdapter
    ): RecyclerView.ViewHolder(itemView){

        val adapter = adapter
        val recipe_image = itemView.recipe_image
        val recipe_name = itemView.recipe_name

        fun bind(recipe:  Recipe){

            val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

            val imageResId = itemView.context.resources.getIdentifier(recipe.imageName, "drawable", itemView.context.packageName)
            Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(imageResId)
                    .into(recipe_image)
            recipe_name.setText(recipe.name)

            itemView.delete_button.setOnClickListener{
                adapter.removeAt(adapterPosition)
            }

            // put image to front of view to make it clickable
            recipe_image.bringToFront()
            recipe_image.setOnClickListener{
                handleClick(recipe)
            }

            recipe_name.bringToFront()
            recipe_name.setOnClickListener{
                handleClick(recipe)
            }

        }

        fun handleClick(recipe: Recipe) {
            Intent(itemView.context, RecipeActivity::class.java).also {
                it.putExtra("EXTRA_NAME", recipe.name)
                it.putExtra("EXTRA_IMAGENAME", recipe.imageName)
                it.putStringArrayListExtra("EXTRA_INGREDIENTS", recipe.ingredients as java.util.ArrayList<String>?)
                it.putStringArrayListExtra("EXTRA_STEPS", recipe.recipeSteps as java.util.ArrayList<String>?)
                it.putExtra("EXTRA_RATING", recipe.rating)
                itemView.context.startActivity(it)
            }
        }

    }

}