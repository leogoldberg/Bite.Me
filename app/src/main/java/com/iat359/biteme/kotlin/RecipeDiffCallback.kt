package com.iat359.biteme.kotlin

import androidx.recyclerview.widget.DiffUtil
import com.iat359.biteme.kotlin.model.Recipe

class RecipeDiffCallback(
        private val old: List<Recipe>,
        private val new: List<Recipe>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
