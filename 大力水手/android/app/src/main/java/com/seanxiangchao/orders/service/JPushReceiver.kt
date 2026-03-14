package com.seanxiangchao.orders.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import org.json.JSONObject

/**
 * 极光推送消息接收器
 */
class JPushReceiver : JPushMessageReceiver() {
    
    companion object {
        private const val TAG = "JPushReceiver"
        
        // 推送类型常量
        const val TYPE_NEW_ORDER = "new_order"          // 新订单
        const val TYPE_ORDER_ASSIGNED = "order_assigned" // 订单分配
        const val TYPE_ORDER_CANCELLED = "order_cancelled" // 订单取消
        const val TYPE_SYSTEM = "system"                // 系统通知
    }
    
    /**
     * 接收到自定义消息（透传消息）
     */
    override fun onMessage(context: Context, message: NotificationMessage) {
        Log.d(TAG, "收到透传消息: ${message.notificationContent}")
        
        try {
            val extras = message.notificationExtras
            val json = JSONObject(extras)
            val type = json.optString("type", "")
            val orderId = json.optString("orderId", "")
            
            when (type) {
                TYPE_NEW_ORDER -> {
                    // 新订单通知
                    Log.d(TAG, "新订单: $orderId")
                    // TODO: 可以发送本地广播更新UI
                }
                TYPE_ORDER_ASSIGNED -> {
                    // 订单分配通知
                    Log.d(TAG, "订单分配: $orderId")
                }
                TYPE_ORDER_CANCELLED -> {
                    // 订单取消通知
                    Log.d(TAG, "订单取消: $orderId")
                }
                TYPE_SYSTEM -> {
                    // 系统通知
                    Log.d(TAG, "系统通知")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析消息失败: ${e.message}")
        }
    }
    
    /**
     * 通知被点击
     */
    override fun onNotifyMessageOpened(context: Context, message: NotificationMessage) {
        Log.d(TAG, "通知被点击: ${message.notificationTitle}")
        
        try {
            val extras = message.notificationExtras
            val json = JSONObject(extras)
            val type = json.optString("type", "")
            val orderId = json.optString("orderId", "")
            
            // 根据类型跳转到不同页面
            when (type) {
                TYPE_NEW_ORDER, TYPE_ORDER_ASSIGNED -> {
                    // 跳转到订单详情
                    // val intent = Intent(context, MainActivity::class.java)
                    // intent.putExtra("orderId", orderId)
                    // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    // context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "处理通知点击失败: ${e.message}")
        }
    }
    
    /**
     * 通知到达
     */
    override fun onNotifyMessageArrived(context: Context, message: NotificationMessage) {
        Log.d(TAG, "通知到达: ${message.notificationTitle}")
    }
    
    /**
     * 通知被清除
     */
    override fun onNotifyMessageDismiss(context: Context, message: NotificationMessage) {
        Log.d(TAG, "通知被清除: ${message.notificationTitle}")
    }
    
    /**
     * 别名设置回调
     */
    override fun onAliasOperatorResult(context: Context, jPushMessage: cn.jpush.android.api.JPushMessage) {
        val sequence = jPushMessage.sequence
        val alias = jPushMessage.alias
        val errorCode = jPushMessage.errorCode
        
        Log.d(TAG, "别名操作结果 - sequence: $sequence, alias: $alias, errorCode: $errorCode")
        
        when (errorCode) {
            0 -> Log.d(TAG, "别名设置成功: $alias")
            else -> Log.e(TAG, "别名设置失败: $errorCode")
        }
    }
    
    /**
     * 标签操作回调
     */
    override fun onTagOperatorResult(context: Context, jPushMessage: cn.jpush.android.api.JPushMessage) {
        val sequence = jPushMessage.sequence
        val tags = jPushMessage.tags
        val errorCode = jPushMessage.errorCode
        
        Log.d(TAG, "标签操作结果 - sequence: $sequence, tags: $tags, errorCode: $errorCode")
        
        when (errorCode) {
            0 -> Log.d(TAG, "标签设置成功: $tags")
            else -> Log.e(TAG, "标签设置失败: $errorCode")
        }
    }
}
