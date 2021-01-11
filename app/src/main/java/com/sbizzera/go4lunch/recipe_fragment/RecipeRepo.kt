package com.sbizzera.go4lunch.recipe_fragment

import com.sbizzera.go4lunch.BuildConfig

class RecipeRepo private constructor(){
    private val recipeApi = getRecipeApi()
    private val spoonacularApiKey= BuildConfig.SPOONACULAR_API_KEY

    companion object {
        val instance: RecipeRepo by lazy { RecipeRepo() }
    }

    fun getRecipe() =recipeApi.getRecipeWithRx(spoonacularApiKey)

}