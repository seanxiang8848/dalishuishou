package com.seanxiangchao.orders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanxiangchao.orders.data.model.DashboardStats
import com.seanxiangchao.orders.data.model.DeliveryStats
import com.seanxiangchao.orders.data.model.TodayStats
import com.seanxiangchao.orders.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class StatsState<T> {
    object Idle : StatsState<Nothing>()
    object Loading : StatsState<Nothing>()
    data class Success<T>(val data: T) : StatsState<T>()
    data class Error(val message: String) : StatsState<Nothing>()
}

class StatsViewModel : ViewModel() {
    
    private val repository = StatsRepository()
    
    private val _dashboardStats = MutableStateFlow<StatsState<DashboardStats>>(StatsState.Idle)
    val dashboardStats: StateFlow<StatsState<DashboardStats>> = _dashboardStats
    
    private val _todayStats = MutableStateFlow<StatsState<TodayStats>>(StatsState.Idle)
    val todayStats: StateFlow<StatsState<TodayStats>> = _todayStats
    
    private val _deliveryStats = MutableStateFlow<StatsState<DeliveryStats>>(StatsState.Idle)
    val deliveryStats: StateFlow<StatsState<DeliveryStats>> = _deliveryStats
    
    fun loadDashboardStats(warehouseId: Int? = null) {
        viewModelScope.launch {
            _dashboardStats.value = StatsState.Loading
            repository.getDashboardStats(warehouseId)
                .onSuccess {
                    _dashboardStats.value = StatsState.Success(it)
                }
                .onFailure {
                    _dashboardStats.value = StatsState.Error(it.message ?: "获取统计数据失败")
                }
        }
    }
    
    fun loadTodayStats(warehouseId: Int? = null) {
        viewModelScope.launch {
            _todayStats.value = StatsState.Loading
            repository.getTodayStats(warehouseId)
                .onSuccess {
                    _todayStats.value = StatsState.Success(it)
                }
                .onFailure {
                    _todayStats.value = StatsState.Error(it.message ?: "获取今日数据失败")
                }
        }
    }
    
    fun loadDeliveryStats(deliveryId: Int, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _deliveryStats.value = StatsState.Loading
            repository.getDeliveryStats(deliveryId, startDate, endDate)
                .onSuccess {
                    _deliveryStats.value = StatsState.Success(it)
                }
                .onFailure {
                    _deliveryStats.value = StatsState.Error(it.message ?: "获取配送员统计失败")
                }
        }
    }
}
