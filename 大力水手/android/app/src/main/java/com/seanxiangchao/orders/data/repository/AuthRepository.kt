package com.seanxiangchao.orders.data.repository

import android.content.Context
import com.seanxiangchao.orders.data.api.ApiClient
import com.seanxiangchao.orders.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    
    private val apiService = ApiClient.getApiService()
    private val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    
    suspend fun login(phone: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(phone, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    data?.let {
                        // 保存Token
                        sharedPref.edit().apply {
                            putString("token", it.token)
                            putInt("userId", it.user.id)
                            putString("userName", it.user.name)
                            putString("userPhone", it.user.phone)
                            putString("userRole", it.user.role)
                            putInt("warehouseId", it.user.warehouseId ?: 0)
                            apply()
                        }
                        Result.success(it)
                    } ?: Result.failure(Exception("登录数据为空"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "登录失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("获取用户信息失败"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "获取用户信息失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.changePassword(ChangePasswordRequest(oldPassword, newPassword))
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "修改密码失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun logout() {
        sharedPref.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPref.getString("token", null) != null
    }
    
    fun getToken(): String? {
        return sharedPref.getString("token", null)
    }
    
    fun getUserId(): Int {
        return sharedPref.getInt("userId", 0)
    }
    
    fun getWarehouseId(): Int {
        return sharedPref.getInt("warehouseId", 0)
    }
}
