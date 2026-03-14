package com.seanxiangchao.orders.data.model

import com.google.gson.annotations.SerializedName

// ==================== 通用响应 ====================

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

// ==================== 用户相关 ====================

data class User(
    val id: Int,
    val phone: String,
    val name: String,
    val role: String,  // admin/manager/delivery
    val avatar: String?,
    val warehouseId: Int?,
    @SerializedName("warehouseName")
    val warehouseName: String?,
    val status: String?  // active/inactive
)

data class LoginRequest(
    val phone: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class RegisterRequest(
    val phone: String,
    val password: String,
    val name: String,
    val role: String = "delivery",
    val warehouseId: Int? = null
)

data class ChangePasswordRequest(
    @SerializedName("oldPassword")
    val oldPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)

// ==================== 订单相关 ====================

data class Order(
    val id: String,
    @SerializedName("orderNo")
    val orderNo: String,
    @SerializedName("customerName")
    val customerName: String,
    @SerializedName("customerPhone")
    val customerPhone: String,
    val address: String,
    val lat: Double?,
    val lng: Double?,
    val status: String,  // pending/assigned/delivering/completed/cancelled
    @SerializedName("productName")
    val productName: String,
    @SerializedName("productTag")
    val productTag: String?,
    val quantity: Int,
    val price: String,
    @SerializedName("totalPrice")
    val totalPrice: String,
    val deposit: Int = 0,
    @SerializedName("oweOrder")
    val oweOrder: Int = 0,
    @SerializedName("oweTicket")
    val oweTicket: Int = 0,
    @SerializedName("oweAmount")
    val oweAmount: String = "0.00",
    @SerializedName("receivable")
    val receivable: String,
    @SerializedName("deliveryIncome")
    val deliveryIncome: String = "0.00",
    val remark: String?,
    @SerializedName("merchantRemark")
    val merchantRemark: String?,
    @SerializedName("warehouseId")
    val warehouseId: Int,
    @SerializedName("warehouseName")
    val warehouseName: String,
    @SerializedName("deliveryId")
    val deliveryId: Int?,
    @SerializedName("deliveryName")
    val deliveryName: String?,
    val channel: String?,  // 线下/抖音/微信
    @SerializedName("orderCount")
    val orderCount: Int = 1,
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("updateTime")
    val updateTime: String?,
    @SerializedName("timeline")
    val timeline: List<OrderTimeline>? = null
)

data class OrderTimeline(
    val time: String,
    val title: String,
    val subtitle: String,
    val active: Boolean = false
)

data class OrderListResponse(
    val list: List<Order>,
    val pagination: Pagination
)

data class CreateOrderRequest(
    @SerializedName("customerName")
    val customerName: String,
    @SerializedName("customerPhone")
    val customerPhone: String,
    val address: String,
    val lat: Double? = null,
    val lng: Double? = null,
    @SerializedName("productName")
    val productName: String,
    val quantity: Int = 1,
    val remark: String? = null,
    @SerializedName("warehouseId")
    val warehouseId: Int
)

data class AssignOrderRequest(
    @SerializedName("deliveryId")
    val deliveryId: Int,
    @SerializedName("warehouseId")
    val warehouseId: Int
)

data class UpdateOrderStatusRequest(
    val status: String,  // pickup/deliver/cancel
    @SerializedName("locationLat")
    val locationLat: Double? = null,
    @SerializedName("locationLng")
    val locationLng: Double? = null,
    @SerializedName("photoUrl")
    val photoUrl: String? = null,
    val note: String? = null
)

data class BatchAssignRequest(
    @SerializedName("orderIds")
    val orderIds: List<String>,
    @SerializedName("deliveryId")
    val deliveryId: Int,
    @SerializedName("warehouseId")
    val warehouseId: Int
)

// ==================== 仓库相关 ====================

data class Warehouse(
    val id: Int,
    val name: String,
    val address: String,
    val lat: Double?,
    val lng: Double?,
    @SerializedName("managerId")
    val managerId: Int?,
    @SerializedName("managerName")
    val managerName: String?,
    @SerializedName("deliveryCount")
    val deliveryCount: Int = 0,
    val status: String = "active"
)

// ==================== 配送员相关 ====================

data class DeliveryStats(
    @SerializedName("totalOrders")
    val totalOrders: Int,
    @SerializedName("totalIncome")
    val totalIncome: String,
    @SerializedName("todayOrders")
    val todayOrders: Int,
    @SerializedName("todayIncome")
    val todayIncome: String,
    @SerializedName("monthOrders")
    val monthOrders: Int,
    @SerializedName("monthIncome")
    val monthIncome: String,
    val rating: Double = 5.0
)

data class UpdateLocationRequest(
    val lat: Double,
    val lng: Double,
    @SerializedName("deliveryId")
    val deliveryId: Int
)

// ==================== 统计数据 ====================

data class DashboardStats(
    @SerializedName("pendingCount")
    val pendingCount: Int,
    @SerializedName("deliveringCount")
    val deliveringCount: Int,
    @SerializedName("todayOrders")
    val todayOrders: Int,
    @SerializedName("todayAmount")
    val todayAmount: String,
    @SerializedName("yesterdayOrders")
    val yesterdayOrders: Int,
    @SerializedName("yesterdayAmount")
    val yesterdayAmount: String,
    @SerializedName("monthOrders")
    val monthOrders: Int,
    @SerializedName("monthAmount")
    val monthAmount: String
)

data class TodayStats(
    @SerializedName("callCount")
    val callCount: Int,
    @SerializedName("onlineIncome")
    val onlineIncome: String,
    @SerializedName("newCustomers")
    val newCustomers: Int,
    @SerializedName("deliveryCount")
    val deliveryCount: Int
)
