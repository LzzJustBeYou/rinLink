package com.rinzelLink

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 主应用类
 * @HiltAndroidApp 注解告诉Hilt这是应用的入口点
 */
@HiltAndroidApp
class RinLinkApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化应用
        initializeApp()
    }
    
    private fun initializeApp() {
        // 这里可以添加应用初始化逻辑
        // 例如：初始化模块管理器、注册服务等
    }
}
