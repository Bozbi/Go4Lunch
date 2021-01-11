package com.sbizzera.go4lunch.recipe_fragment

import com.google.gson.annotations.SerializedName

data class Recipe (
        val title:String,
        val instructions:String,
        @SerializedName("image")
        val imageUrl:String,
        @SerializedName("readyInMinutes")
        val cookTime:Int
)

data class RecipeApiResponse(
        @SerializedName("recipes")
        val recipes:List<Recipe>
)
