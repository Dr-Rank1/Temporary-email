package com.rank.tempbox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.mail.tm/"
    private const val TAG = "TempBox-API"

    private val _retryAfter = MutableLiveData<Int>()
    val retryAfter: LiveData<Int> = _retryAfter

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    }

    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    private val retryAfterInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (response.code == 429) {
            val retryAfterSeconds = response.header("Retry-After")?.toIntOrNull() ?: 3
            _retryAfter.postValue(retryAfterSeconds)
            Log.w(TAG, "Rate limited (429) — Retry-After: ${retryAfterSeconds}s")
        } else {
            _retryAfter.postValue(0)
        }
        response
    }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(retryAfterInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val unauthenticatedClient: OkHttpClient by lazy { buildOkHttpClient() }
    val authenticatedClient: OkHttpClient by lazy { buildOkHttpClient() }

    val unauthenticatedApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(unauthenticatedClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val authenticatedApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
