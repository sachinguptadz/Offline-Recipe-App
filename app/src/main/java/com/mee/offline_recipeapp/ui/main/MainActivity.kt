package com.mee.offline_recipeapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.mee.offline_recipeapp.App
import com.mee.offline_recipeapp.databinding.ActivityMainBinding
import com.mee.offline_recipeapp.ui.common.MainVmFactory
import com.mee.offline_recipeapp.ui.detail.DetailActivity
import com.mee.offline_recipeapp.utils.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var showedFirstSyncDialog = false
    private lateinit var b: ActivityMainBinding
    private val vm by viewModels<MainViewModel> { MainVmFactory(application as App) }
    private val adapter = MealsAdapter { meal ->
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ID, meal.idMeal)

            putExtra(DetailActivity.EXTRA_TITLE, meal.name)

            putExtra(DetailActivity.EXTRA_THUMB, meal.thumb)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)
        b.recycler.adapter = adapter
        b.swipe.setOnRefreshListener { vm.refresh() }

        lifecycleScope.launch {
            vm.state.collectLatest { st ->
                when (st) {
                    is ListState.Loading -> {
                        b.progress.isVisible = true
                        b.empty.isVisible = false

                    }
                    is ListState.Data -> {
                        b.progress.isVisible = false
                        b.empty.isVisible = false
                        adapter.submitList(st.items)
                        b.swipe.isRefreshing = false
                        showedFirstSyncDialog = true

                    }
                    is ListState.Error -> {
                        b.progress.isVisible = false
                        b.empty.isVisible = true
                        b.empty.text = "No cached data yet."
                        b.swipe.isRefreshing = false
                        if (!showedFirstSyncDialog) {

                            showFirstSyncDialog(st.message)
                        }
                    }
                }
            }
        }

        vm.initialLoad()
    }

    private fun showFirstSyncDialog(message: String) {
        showedFirstSyncDialog = true
        AlertDialog.Builder(this)
            .setTitle("First Sync Required")

            .setMessage("$message\n\nAfter the first sync, the app will work offline.")
            .setCancelable(false)
            .setPositiveButton("Open Internet Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                showedFirstSyncDialog = false
            }
            .setNegativeButton("Retry") { _, _ ->
                if (NetworkUtils.isInternetAvailable(this)) {
                    vm.refresh()
                } else {
                    showedFirstSyncDialog = false
                    showFirstSyncDialog("Still offline. Please turn on internet.")
                }
            }
            .show()
    }
}