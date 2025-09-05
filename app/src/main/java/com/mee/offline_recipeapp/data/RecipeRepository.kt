package com.mee.offline_recipeapp.data

import com.mee.offline_recipeapp.data.db.AppDatabase
import com.mee.offline_recipeapp.data.db.MealEntity
import com.mee.offline_recipeapp.data.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RecipeRepository(private val db: AppDatabase) {
    private val api = Network.api
    private val dao = db.mealDao()

    fun observeList(): Flow<List<MealEntity>> = dao.observeAll()

    suspend fun refreshList(letter: String = "a") = withContext(Dispatchers.IO) {
        val resp = api.searchByFirstLetter(letter)

        val mapped = resp.meals.orEmpty().mapNotNull {
            val id = it.idMeal ?: return@mapNotNull null
            val name = it.strMeal ?: return@mapNotNull null
            MealEntity(
                idMeal = id,
                name = name,
                thumb = it.strMealThumb,

                category = it.strCategory,
                area = it.strArea,

                instructions = null
            )
        }
        if (mapped.isNotEmpty()) {
            dao.clearAll()
            dao.upsertAll(mapped)

        } else {
            dao.clearAll()
        }
    }

    fun observeDetail(id: String) = dao.observeById(id)

    suspend fun ensureDetail(id: String) = withContext(Dispatchers.IO) {
        val resp = api.lookupById(id)
        val dto = resp.meals?.firstOrNull() ?: return@withContext
        val entity = MealEntity(
            idMeal = dto.idMeal ?: id,
            name = dto.strMeal ?: "",

            thumb = dto.strMealThumb,
            category = dto.strCategory,

            area = dto.strArea,
            instructions = dto.strInstructions
        )
        dao.upsert(entity)
    }
}