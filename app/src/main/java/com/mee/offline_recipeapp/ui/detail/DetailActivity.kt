package com.mee.offline_recipeapp.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.mee.offline_recipeapp.App
import com.mee.offline_recipeapp.databinding.ActivityDetailBinding
import com.mee.offline_recipeapp.ui.common.DetailVmFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_THUMB = "thumb"

    }

    private lateinit var b: ActivityDetailBinding

    private val vm by viewModels<DetailViewModel> {

        DetailVmFactory(application as App, intent.getStringExtra(EXTRA_ID)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(b.root)


        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = intent.getStringExtra(EXTRA_TITLE) ?: "Recipe"

        b.image.load(intent.getStringExtra(EXTRA_THUMB))

        lifecycleScope.launch {
            vm.state.collectLatest { st ->
                when (st) {
                    is DetailState.Loading -> {
                        b.progress.isVisible = true
                        b.content.isVisible = false
                        b.error.isVisible = false
                    }
                    is DetailState.Data -> {
                        b.progress.isVisible = false
                        b.content.isVisible = true

                        b.error.isVisible = false
                        val m = st.meal
                        b.title.text = m.name
                        b.meta.text = listOfNotNull(m.category, m.area).joinToString(" • ")
                        b.instructions.text = m.instructions ?: "—"
                        b.image.load(m.thumb)
                    }
                    is DetailState.Error -> {
                        b.progress.isVisible = false
                        b.content.isVisible = false
                        b.error.isVisible = true
                        b.error.text = st.message

                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}