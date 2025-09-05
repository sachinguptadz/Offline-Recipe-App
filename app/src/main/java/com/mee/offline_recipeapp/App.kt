package com.mee.offline_recipeapp

import android.app.Application
import com.mee.offline_recipeapp.data.db.AppDatabase
import com.mee.offline_recipeapp.data.network.Network

class App : Application() {
    lateinit var db: AppDatabase; private set

    override fun onCreate() {
        super.onCreate()
        
        Network.init(this)
        db = AppDatabase.get(this)
    }
}