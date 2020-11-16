package com.iat359.biteme

data class Recipe (
        val id: Long,
        val name: String,
        val imageName: String,
        val ingredients: List<String>,
        val recipeSteps: List<String>,
        val rating: Float
) {

}