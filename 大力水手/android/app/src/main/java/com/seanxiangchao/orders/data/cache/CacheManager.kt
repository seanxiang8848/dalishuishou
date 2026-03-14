package com.seanxiangchao.orders.data.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seanxiangchao.orders.data.model.Order
import com.seanxiangchao.orders.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_cache")

/**
 * 应用缓存管理器
 * 用于缓存常用数据，减少网络请求
 */
class CacheManager(private val context: Context) {
    
    private val gson = Gson()
    
    companion object {
        // Keys
        private val USER_CACHE_KEY = stringPreferencesKey("user_cache")
        private val USER_CACHE_TIME_KEY = longPreferencesKey("user_cache_time")
        private val ORDERS_CACHE_KEY = stringPreferencesKey("orders_cache")
        private val ORDERS_CACHE_TIME_KEY = longPreferencesKey("orders_cache_time")
        private val STATS_CACHE_KEY = stringPreferencesKey("stats_cache")
        private val STATS_CACHE_TIME_KEY = longPreferencesKey("stats_cache_time")
        
        // 缓存有效期（毫秒）
        private const val USER_CACHE_DURATION = 30 * 60 * 1000L  // 30分钟
        private const val ORDERS_CACHE_DURATION = 5 * 60 * 1000L   // 5分钟
        private const val STATS_CACHE_DURATION = 2 * 60 * 1000L    // 2分钟
    }
    
    // ==================== 用户缓存 ====================
    
    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[USER_CACHE_KEY] = gson.toJson(user)
            prefs[USER_CACHE_TIME_KEY] = System.currentTimeMillis()
        }
    }
    
    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[USER_CACHE_KEY]
            val cacheTime = prefs[USER_CACHE_TIME_KEY] ?: 0
            
            if (json != null && isCacheValid(cacheTime, USER_CACHE_DURATION)) {
                gson.fromJson(json, User::class.java)
            } else {
                null
            }
        }
    }
    
    // ==================== 订单缓存 ====================
    
    suspend fun saveOrders(orders: List<Order>) {
        context.dataStore.edit { prefs ->
            prefs[ORDERS_CACHE_KEY] = gson.toJson(orders)
            prefs[ORDERS_CACHE_TIME_KEY] = System.currentTimeMillis()
        }
    }
    
    fun getOrders(): Flow<List<Order>?> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[ORDERS_CACHE_KEY]
            val cacheTime = prefs[ORDERS_CACHE_TIME_KEY] ?: 0
            
            if (json != null && isCacheValid(cacheTime, ORDERS_CACHE_DURATION)) {
                val type = object : TypeToken<List<Order>>() {}.type
                gson.fromJson<List<Order>>(json, type)
            } else {
                null
            }
        }
    }
    
    // ==================== 统计缓存 ====================
    
    suspend fun saveStats(stats: DashboardStatsCache) {
        context.dataStore.edit { prefs ->
            prefs[STATS_CACHE_KEY] = gson.toJson(stats)
            prefs[STATS_CACHE_TIME_KEY] = System.currentTimeMillis()
        }
    }
    
    fun getStats(): Flow<DashboardStatsCache?> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[STATS_CACHE_KEY]
            val cacheTime = prefs[STATS_CACHE_TIME_KEY] ?: 0
            
            if (json != null && isCacheValid(cacheTime, STATS_CACHE_DURATION)) {
                gson.fromJson(json, DashboardStatsCache::class.java)
            } else {
                null
            }
        }
    }
    
    // ==================== 清除缓存 ====================
    
    suspend fun clearCache() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    
    suspend fun clearUserCache() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_CACHE_KEY)
            prefs.remove(USER_CACHE_TIME_KEY)
        }
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 检查缓存是否有效
     */
    private fun isCacheValid(cacheTime: Long, duration: Long): Boolean {
        return System.currentTimeMillis() - cacheTime < duration
    }
}

/**
 * 统计数据缓存类
 */
data class DashboardStatsCache(
    val pendingCount: Int,
    val deliveringCount: Int,
    val todayOrders: Int,
    val todayAmount: String,
    val yesterdayOrders: Int,
    val yesterdayAmount: String,
    val monthOrders: Int,
    val monthAmount: String,
    val cacheTime: Long = System.currentTimeMillis()
)
