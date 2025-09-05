package com.mee.offline_recipeapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mee.offline_recipeapp.data.RecipeRepository
import com.mee.offline_recipeapp.data.db.MealEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ListState {
    object Loading : ListState
    data class Data(val items: List<MealEntity>) : ListState
    data class Error(val message: String) : ListState
}


class MainViewModel(private val repo: RecipeRepository) : ViewModel() {
    private val dbFlow = repo.observeList()
    private val _loading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val state: StateFlow<ListState> = combine(dbFlow, _loading, _error) { list, loading, err ->
        when {
            list.isNotEmpty() -> ListState.Data(list)
            loading && err == null -> ListState.Loading

            list.isEmpty() && err != null -> ListState.Error(err)
            else -> ListState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListState.Loading)

    fun initialLoad() = refresh()

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                repo.refreshList("a")
            } catch (e: Exception) {

                _error.value = "Internet is not available. Turn it on to complete first sync."
            } finally {
                _loading.value = false
            }
        }
    }
}