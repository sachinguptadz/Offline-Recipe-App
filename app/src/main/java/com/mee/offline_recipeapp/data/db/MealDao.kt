package com.mee.offline_recipeapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY name ASC")
    fun observeAll(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE idMeal = :id LIMIT 1")
    fun observeById(id: String): Flow<MealEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MealEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MealEntity)

    @Query("DELETE FROM meals")
    suspend fun clearAll()
}