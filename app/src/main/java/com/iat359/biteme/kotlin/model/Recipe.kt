package com.iat359.biteme.kotlin.model

data class Recipe (
        var id: Long,
        var name: String,
        var imageName: String,
        var ingredients: List<String>,
        var recipeSteps: List<String>
) {

}