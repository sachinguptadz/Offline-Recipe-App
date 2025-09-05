package com.mee.offline_recipeapp.data.network

import com.mee.offline_recipeapp.data.network.dto.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    // list by first letter (we’ll send "a")
    @GET("search.php")
    suspend fun searchByFirstLetter(@Query("f") letter: String): MealsResponse

    // detail by id
    @GET("lookup.php")
    suspend fun lookupById(@Query("i") id: String): MealsResponse
}