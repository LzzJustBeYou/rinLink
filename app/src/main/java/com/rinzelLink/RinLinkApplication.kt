package com.rinzelLink

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用程序入口类
 * @HiltAndroidApp 注解告诉Hilt这是应用程序的入口点
 */
@HiltAndroidApp
class RinLinkApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化应用程序
        initializeApp()
    }
    
    private fun initializeApp() {
        // 这里可以添加应用程序级别的初始化代码
        // 例如：初始化日志系统、崩溃报告、分析工具等
    }
}
