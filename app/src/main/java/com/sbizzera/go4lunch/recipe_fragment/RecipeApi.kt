package com.sbizzera.go4lunch.recipe_fragment

import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val SERVICE : RecipeApi by lazy{
    val okHttpClient = OkHttpClient.Builder().build()
    val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    retrofit.create(RecipeApi::class.java)
}

fun getRecipeApi() = SERVICE

interface RecipeApi{
    @GET("recipes/random/?")
    fun getRecipeWithRx(@Query("apiKey")apiKey:String):Observable<RecipeApiResponse>
}

