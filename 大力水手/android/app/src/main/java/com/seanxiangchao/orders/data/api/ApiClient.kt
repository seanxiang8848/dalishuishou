package com.seanxiangchao.orders.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // 开发环境 - Android模拟器访问本机
    // const val BASE_URL = "http://10.0.2.2:3000/api/"
    
    // 生产环境
    const val BASE_URL = "http://seanxiangchao.top/api/"
    
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
    
    fun initialize(context: Context) {
        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPref))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit?.create(ApiService::class.java)
    }
    
    fun getApiService(): ApiService {
        return apiService ?: throw IllegalStateException("ApiClient not initialized. Call initialize() first.")
    }
}

// Token拦截器
class AuthInterceptor(private val sharedPref: android.content.SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = sharedPref.getString("token", null)
        val request = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
            addHeader("Content-Type", "application/json")
        }.build()
        return chain.proceed(request)
    }
}
