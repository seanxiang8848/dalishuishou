package com.seanxiangchao.orders.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seanxiangchao.orders.data.api.ApiClient
import com.seanxiangchao.orders.data.model.LoginResponse
import com.seanxiangchao.orders.data.model.User
import com.seanxiangchao.orders.data.repository.AuthRepository
import com.seanxiangchao.orders.service.JPushManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val loginResponse: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = AuthRepository(application)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    private val _isLoggedIn = MutableStateFlow(repository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    
    init {
        // 如果已登录，加载用户信息并设置推送别名
        if (repository.isLoggedIn()) {
            loadUserProfile()
        }
    }
    
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(phone, password)
                .onSuccess {
                    _authState.value = AuthState.Success(it)
                    _user.value = it.user
                    _isLoggedIn.value = true
                    
                    // 设置极光推送别名
                    JPushManager.setAlias(getApplication(), it.user.id)
                    it.user.warehouseId?.let { warehouseId ->
                        JPushManager.addTags(getApplication(), warehouseId)
                    }
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "登录失败")
                }
        }
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            repository.getProfile()
                .onSuccess { user ->
                    _user.value = user
                    // 设置推送别名和标签
                    JPushManager.setAlias(getApplication(), user.id)
                    user.warehouseId?.let {
                        JPushManager.addTags(getApplication(), it)
                    }
                }
                .onFailure {
                    // 如果获取用户信息失败，可能是token过期
                    logout()
                }
        }
    }
    
    fun logout() {
        // 清除推送别名和标签
        JPushManager.deleteAlias(getApplication())
        JPushManager.cleanTags(getApplication())
        
        repository.logout()
        _user.value = null
        _isLoggedIn.value = false
        _authState.value = AuthState.Idle
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
