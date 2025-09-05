package com.mee.offline_recipeapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mee.offline_recipeapp.data.db.MealEntity
import com.mee.offline_recipeapp.databinding.ItemMealBinding

class MealsAdapter(
    private val onClick: (MealEntity) -> Unit
) : ListAdapter<MealEntity, MealsAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<MealEntity>() {
        override fun areItemsTheSame(o: MealEntity, n: MealEntity) = o.idMeal == n.idMeal
        override fun areContentsTheSame(o: MealEntity, n: MealEntity) = o == n
    }

    inner class VH(val b: ItemMealBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.b.title.text = item.name
        h.b.thumbnail.load(item.thumb)
        h.b.root.setOnClickListener { onClick(item) }
    }
}