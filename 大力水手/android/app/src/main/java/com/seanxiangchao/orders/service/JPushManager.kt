package com.seanxiangchao.orders.service

import android.app.Application
import android.util.Log
import cn.jpush.android.api.JPushInterface

object JPushManager {
    private const val TAG = "JPushManager"
    
    fun init(application: Application) {
        // 开启调试模式（开发环境设为true，生产环境设为false）
        JPushInterface.setDebugMode(true)
        
        // 初始化极光推送
        JPushInterface.init(application)
        
        Log.d(TAG, "极光推送初始化完成")
        Log.d(TAG, "RegistrationID: ${JPushInterface.getRegistrationID(application)}")
    }
    
    /**
     * 设置别名（用于给特定用户推送）
     * @param userId 用户ID
     */
    fun setAlias(application: Application, userId: Int) {
        val alias = "user_$userId"
        JPushInterface.setAlias(application, 0, alias)
        Log.d(TAG, "设置别名: $alias")
    }
    
    /**
     * 删除别名
     */
    fun deleteAlias(application: Application) {
        JPushInterface.deleteAlias(application, 0)
        Log.d(TAG, "删除别名")
    }
    
    /**
     * 添加标签（用于分组推送）
     * @param warehouseId 仓库ID
     */
    fun addTags(application: Application, warehouseId: Int) {
        val tags = setOf("warehouse_$warehouseId")
        JPushInterface.addTags(application, 0, tags)
        Log.d(TAG, "添加标签: $tags")
    }
    
    /**
     * 删除标签
     */
    fun cleanTags(application: Application) {
        JPushInterface.cleanTags(application, 0)
        Log.d(TAG, "清除标签")
    }
    
    /**
     * 停止推送（退出登录时调用）
     */
    fun stopPush(application: Application) {
        JPushInterface.stopPush(application)
        Log.d(TAG, "停止推送")
    }
    
    /**
     * 恢复推送（登录后调用）
     */
    fun resumePush(application: Application) {
        JPushInterface.resumePush(application)
        Log.d(TAG, "恢复推送")
    }
    
    /**
     * 获取 RegistrationID
     */
    fun getRegistrationID(application: Application): String {
        return JPushInterface.getRegistrationID(application) ?: ""
    }
}
