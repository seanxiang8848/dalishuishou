package com.seanxiangchao.orders.data.repository

import com.seanxiangchao.orders.data.api.ApiClient
import com.seanxiangchao.orders.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository {
    
    private val apiService = ApiClient.getApiService()
    
    suspend fun getOrders(
        page: Int = 1,
        limit: Int = 20,
        status: String? = null,
        warehouseId: Int? = null,
        keyword: String? = null
    ): Result<OrderListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrders(
                    page = page,
                    limit = limit,
                    status = status,
                    warehouseId = warehouseId,
                    keyword = keyword
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("数据为空"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取订单列表失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getOrderDetail(orderId: String): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrderDetail(orderId)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("订单详情为空"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取订单详情失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun assignOrder(orderId: String, deliveryId: Int, warehouseId: Int): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.assignOrder(
                    orderId,
                    AssignOrderRequest(deliveryId, warehouseId)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("派单失败"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "派单失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateOrderStatus(
        orderId: String,
        status: String,
        note: String? = null,
        photoUrl: String? = null
    ): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateOrderStatus(
                    orderId,
                    UpdateOrderStatusRequest(status = status, note = note, photoUrl = photoUrl)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("更新状态失败"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "更新状态失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCurrentOrders(deliveryId: Int): Result<List<Order>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentOrders(deliveryId)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取当前订单失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createOrder(request: CreateOrderRequest): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createOrder(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("创建订单失败"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "创建订单失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
