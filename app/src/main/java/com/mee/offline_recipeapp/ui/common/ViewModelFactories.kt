package com.mee.offline_recipeapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mee.offline_recipeapp.App
import com.mee.offline_recipeapp.data.RecipeRepository
import com.mee.offline_recipeapp.ui.detail.DetailViewModel
import com.mee.offline_recipeapp.ui.main.MainViewModel

@Suppress("UNCHECKED_CAST")
class MainVmFactory(app: App) : ViewModelProvider.Factory {
    private val repo = RecipeRepository(app.db)

    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repo) as T
}

@Suppress("UNCHECKED_CAST")
class DetailVmFactory(app: App, private val id: String) : ViewModelProvider.Factory {

    private val repo = RecipeRepository(app.db)
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailViewModel(id, repo) as T
}