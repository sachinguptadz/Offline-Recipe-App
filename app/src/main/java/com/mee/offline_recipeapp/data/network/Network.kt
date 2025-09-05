package com.mee.offline_recipeapp.data.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object Network {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    lateinit var api: MealApi; private set

    fun init(ctx: Context) {
        val cache = Cache(File(ctx.cacheDir, "http"), 5L * 1024 * 1024) // 5MB
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

        val client = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val req = chain.request()
                val res = chain.proceed(req)


                res.newBuilder().header("Cache-Control", "public, max-age=60").build()
            }
            .addInterceptor(logger)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)

            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(MealApi::class.java)
    }
}