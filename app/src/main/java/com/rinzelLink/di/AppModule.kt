package com.rinzelLink.di

import com.rinzelLink.core.module.ModuleManager
import com.rinzelLink.core.room.RoomManager
import com.rinzelLink.core.group.DeviceGroupManager
import com.rinzelLink.core.scene.SceneService
import com.rinzelLink.modules.ble.BLEService
import com.rinzelLink.modules.lan.LANService
import com.rinzelLink.modules.mqtt.MQTTService
import com.rinzelLink.modules.websocket.WebSocketService
import com.rinzelLink.modules.zigbee.ZigbeeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 应用模块 - Hilt依赖注入配置
 * 负责提供应用级别的依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * 提供模块管理器
     */
    @Provides
    @Singleton
    fun provideModuleManager(): ModuleManager {
        return ModuleManager()
    }
    
    /**
     * 提供房间管理器
     */
    @Provides
    @Singleton
    fun provideRoomManager(): RoomManager {
        return RoomManager()
    }
    
    /**
     * 提供设备分组管理器
     */
    @Provides
    @Singleton
    fun provideDeviceGroupManager(): DeviceGroupManager {
        return DeviceGroupManager()
    }
    
    /**
     * 提供场景服务
     */
    @Provides
    @Singleton
    fun provideSceneService(): SceneService {
        return SceneService()
    }
    
    /**
     * 提供LAN服务
     */
    @Provides
    @Singleton
    fun provideLANService(): LANService {
        return LANService()
    }
    
    /**
     * 提供Zigbee服务
     */
    @Provides
    @Singleton
    fun provideZigbeeService(): ZigbeeService {
        return ZigbeeService()
    }
    
    /**
     * 提供WebSocket服务
     */
    @Provides
    @Singleton
    fun provideWebSocketService(): WebSocketService {
        return WebSocketService()
    }
    
    /**
     * 提供BLE服务
     */
    @Provides
    @Singleton
    fun provideBLEService(): BLEService {
        return BLEService()
    }
    
    /**
     * 提供MQTT服务
     */
    @Provides
    @Singleton
    fun provideMQTTService(): MQTTService {
        return MQTTService()
    }
}
