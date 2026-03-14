package com.seanxiangchao.orders.data.api

import com.seanxiangchao.orders.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ==================== 认证接口 ====================
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<User>>
    
    @GET("auth/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>
    
    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>
    
    // ==================== 订单接口 ====================
    
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("status") status: String? = null,
        @Query("warehouseId") warehouseId: Int? = null,
        @Query("deliveryId") deliveryId: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<OrderListResponse>>
    
    @GET("orders/{id}")
    suspend fun getOrderDetail(@Path("id") id: String): Response<ApiResponse<Order>>
    
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<Order>>
    
    @POST("orders/{id}/assign")
    suspend fun assignOrder(
        @Path("id") id: String,
        @Body request: AssignOrderRequest
    ): Response<ApiResponse<Order>>
    
    @POST("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<ApiResponse<Order>>
    
    @GET("orders/delivery/current")
    suspend fun getCurrentOrders(
        @Query("deliveryId") deliveryId: Int
    ): Response<ApiResponse<List<Order>>>
    
    @POST("orders/batch-assign")
    suspend fun batchAssignOrders(@Body request: BatchAssignRequest): Response<ApiResponse<Unit>>
    
    // ==================== 仓库接口 ====================
    
    @GET("warehouses")
    suspend fun getWarehouses(): Response<ApiResponse<List<Warehouse>>>
    
    @GET("warehouses/{id}")
    suspend fun getWarehouseDetail(@Path("id") id: Int): Response<ApiResponse<Warehouse>>
    
    // ==================== 配送员接口 ====================
    
    @GET("delivery/stats")
    suspend fun getDeliveryStats(
        @Query("deliveryId") deliveryId: Int,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<DeliveryStats>>
    
    @POST("delivery/location")
    suspend fun updateLocation(@Body request: UpdateLocationRequest): Response<ApiResponse<Unit>>
    
    @GET("delivery/history")
    suspend fun getDeliveryHistory(
        @Query("deliveryId") deliveryId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Order>>>
    
    // ==================== 统计数据接口 ====================
    
    @GET("stats/dashboard")
    suspend fun getDashboardStats(
        @Query("warehouseId") warehouseId: Int? = null
    ): Response<ApiResponse<DashboardStats>>
    
    @GET("stats/today")
    suspend fun getTodayStats(
        @Query("warehouseId") warehouseId: Int? = null
    ): Response<ApiResponse<TodayStats>>
}
