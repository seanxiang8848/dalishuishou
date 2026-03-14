package com.seanxiangchao.orders.data.repository

import com.seanxiangchao.orders.data.api.ApiClient
import com.seanxiangchao.orders.data.model.DashboardStats
import com.seanxiangchao.orders.data.model.DeliveryStats
import com.seanxiangchao.orders.data.model.TodayStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatsRepository {
    
    private val apiService = ApiClient.getApiService()
    
    suspend fun getDashboardStats(warehouseId: Int? = null): Result<DashboardStats> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDashboardStats(warehouseId)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("统计数据为空"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取统计数据失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getTodayStats(warehouseId: Int? = null): Result<TodayStats> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTodayStats(warehouseId)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("今日数据为空"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取今日数据失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getDeliveryStats(
        deliveryId: Int,
        startDate: String? = null,
        endDate: String? = null
    ): Result<DeliveryStats> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDeliveryStats(deliveryId, startDate, endDate)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("配送员统计数据为空"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取配送员统计失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
