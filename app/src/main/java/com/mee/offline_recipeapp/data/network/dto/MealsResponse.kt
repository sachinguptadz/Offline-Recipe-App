package com.mee.offline_recipeapp.data.network.dto

data class MealsResponse(val meals: List<MealDto>?)

data class MealDto(
    val idMeal: String?,
    val strMeal: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?
)