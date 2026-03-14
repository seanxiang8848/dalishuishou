package com.seanxiangchao.orders

import android.app.Application
import com.seanxiangchao.orders.data.api.ApiClient
import com.seanxiangchao.orders.service.JPushManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    
    companion object {
        lateinit var instance: MyApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化网络客户端
        ApiClient.initialize(this)
        
        // 初始化极光推送
        JPushManager.init(this)
    }
}
