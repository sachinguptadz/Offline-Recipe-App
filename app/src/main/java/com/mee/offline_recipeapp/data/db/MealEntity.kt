package com.mee.offline_recipeapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val idMeal: String,
    val name: String,
    val thumb: String?,

    val category: String?,
    val area: String?,
    val instructions: String?
)