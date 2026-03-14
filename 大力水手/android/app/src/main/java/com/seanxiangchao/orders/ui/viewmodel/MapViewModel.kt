package com.seanxiangchao.orders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MapState {
    object Idle : MapState()
    object Loading : MapState()
    data class Success(
        val route: RouteInfo,
        val polylines: List<LatLng>
    ) : MapState()
    data class Error(val message: String) : MapState()
}

data class RouteInfo(
    val distance: String,      // 总距离
    val duration: String,      // 总时间
    val tolls: String,         // 过路费
    val trafficLights: Int,    // 红绿灯数量
    val steps: List<RouteStep>
)

data class RouteStep(
    val instruction: String,   // 行驶指示
    val distance: String,      // 步骤距离
    val duration: String,      // 步骤时间
    val action: String,        // 动作（左转/右转等）
    val polyline: List<LatLng>
)

class MapViewModel : ViewModel() {
    
    private val _mapState = MutableStateFlow<MapState>(MapState.Idle)
    val mapState: StateFlow<MapState> = _mapState
    
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation
    
    /**
     * 规划路线
     */
    fun planRoute(
        start: LatLng,
        end: LatLng,
        waypoints: List<LatLng> = emptyList()
    ) {
        viewModelScope.launch {
            _mapState.value = MapState.Loading
            
            try {
                // TODO: 调用高德地图路线规划API
                // 这里先模拟数据
                val mockRoute = RouteInfo(
                    distance = "7公里",
                    duration = "19分钟",
                    tolls = "¥0",
                    trafficLights = 5,
                    steps = listOf()
                )
                
                val mockPolylines = listOf(start) + waypoints + listOf(end)
                
                _mapState.value = MapState.Success(mockRoute, mockPolylines)
                
            } catch (e: Exception) {
                _mapState.value = MapState.Error(e.message ?: "路线规划失败")
            }
        }
    }
    
    /**
     * 更新当前位置
     */
    fun updateCurrentLocation(location: LatLng) {
        _currentLocation.value = location
    }
    
    /**
     * 搜索地址
     */
    fun searchAddress(keyword: String) {
        viewModelScope.launch {
            // TODO: 调用高德地图搜索API
        }
    }
    
    fun clearError() {
        if (_mapState.value is MapState.Error) {
            _mapState.value = MapState.Idle
        }
    }
}
