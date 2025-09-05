package com.mee.offline_recipeapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mee.offline_recipeapp.data.RecipeRepository
import com.mee.offline_recipeapp.data.db.MealEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetailState {
    object Loading : DetailState
    data class Data(val meal: MealEntity) : DetailState
    data class Error(val message: String) : DetailState
}

class DetailViewModel(
    private val id: String,
    private val repo: RecipeRepository
) : ViewModel() {

    private val trigger = MutableStateFlow(Unit)

    val state: StateFlow<DetailState> = trigger
        .flatMapLatest { repo.observeDetail(id) }
        .map { meal ->
            if (meal == null || meal.instructions == null) {


                viewModelScope.launch { runCatching { repo.ensureDetail(id) } }
                DetailState.Loading

            } else DetailState.Data(meal)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DetailState.Loading)
}