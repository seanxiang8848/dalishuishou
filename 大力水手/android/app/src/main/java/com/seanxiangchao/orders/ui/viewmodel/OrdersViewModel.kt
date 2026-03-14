package com.seanxiangchao.orders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanxiangchao.orders.data.model.Order
import com.seanxiangchao.orders.data.model.OrderListResponse
import com.seanxiangchao.orders.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrdersState {
    object Idle : OrdersState()
    object Loading : OrdersState()
    data class Success(val data: OrderListResponse) : OrdersState()
    data class Error(val message: String) : OrdersState()
}

sealed class OrderDetailState {
    object Idle : OrderDetailState()
    object Loading : OrderDetailState()
    data class Success(val order: Order) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}

class OrdersViewModel : ViewModel() {
    
    private val repository = OrderRepository()
    
    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Idle)
    val ordersState: StateFlow<OrdersState> = _ordersState
    
    private val _orderDetailState = MutableStateFlow<OrderDetailState>(OrderDetailState.Idle)
    val orderDetailState: StateFlow<OrderDetailState> = _orderDetailState
    
    private val _currentOrders = MutableStateFlow<List<Order>>(emptyList())
    val currentOrders: StateFlow<List<Order>> = _currentOrders
    
    private var currentPage = 1
    private var isLoadingMore = false
    
    fun loadOrders(
        page: Int = 1,
        status: String? = null,
        warehouseId: Int? = null,
        keyword: String? = null
    ) {
        viewModelScope.launch {
            if (page == 1) {
                _ordersState.value = OrdersState.Loading
            }
            
            repository.getOrders(page = page, status = status, warehouseId = warehouseId, keyword = keyword)
                .onSuccess {
                    _ordersState.value = OrdersState.Success(it)
                    currentPage = page
                }
                .onFailure {
                    _ordersState.value = OrdersState.Error(it.message ?: "获取订单失败")
                }
        }
    }
    
    fun loadMoreOrders(status: String? = null, warehouseId: Int? = null) {
        if (isLoadingMore) return
        
        viewModelScope.launch {
            isLoadingMore = true
            val nextPage = currentPage + 1
            
            repository.getOrders(page = nextPage, status = status, warehouseId = warehouseId)
                .onSuccess { response ->
                    val currentData = (_ordersState.value as? OrdersState.Success)?.data
                    if (currentData != null) {
                        val mergedList = currentData.list + response.list
                        val mergedResponse = response.copy(list = mergedList)
                        _ordersState.value = OrdersState.Success(mergedResponse)
                    } else {
                        _ordersState.value = OrdersState.Success(response)
                    }
                    currentPage = nextPage
                }
                .onFailure {
                    // 加载更多失败不更新状态，保持原有数据
                }
            
            isLoadingMore = false
        }
    }
    
    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _orderDetailState.value = OrderDetailState.Loading
            repository.getOrderDetail(orderId)
                .onSuccess {
                    _orderDetailState.value = OrderDetailState.Success(it)
                }
                .onFailure {
                    _orderDetailState.value = OrderDetailState.Error(it.message ?: "获取订单详情失败")
                }
        }
    }
    
    fun loadCurrentOrders(deliveryId: Int) {
        viewModelScope.launch {
            repository.getCurrentOrders(deliveryId)
                .onSuccess {
                    _currentOrders.value = it
                }
        }
    }
    
    fun assignOrder(orderId: String, deliveryId: Int, warehouseId: Int) {
        viewModelScope.launch {
            repository.assignOrder(orderId, deliveryId, warehouseId)
                .onSuccess {
                    _orderDetailState.value = OrderDetailState.Success(it)
                }
                .onFailure {
                    _orderDetailState.value = OrderDetailState.Error(it.message ?: "派单失败")
                }
        }
    }
    
    fun updateOrderStatus(orderId: String, status: String, note: String? = null) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, note)
                .onSuccess {
                    _orderDetailState.value = OrderDetailState.Success(it)
                }
                .onFailure {
                    _orderDetailState.value = OrderDetailState.Error(it.message ?: "更新状态失败")
                }
        }
    }
    
    fun clearError() {
        if (_ordersState.value is OrdersState.Error) {
            _ordersState.value = OrdersState.Idle
        }
        if (_orderDetailState.value is OrderDetailState.Error) {
            _orderDetailState.value = OrderDetailState.Idle
        }
    }
}
