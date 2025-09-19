package com.rinzelLink.core.module

import android.content.Context
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Property

/**
 * 服务模块接口
 * 所有协议模块都需要实现此接口
 */
interface MiServiceModule {
    val name: String
    val protocol: com.rinzelLink.core.device.Protocol
    val isAvailable: Boolean
    val isConnected: Boolean
    
    /**
     * 初始化模块
     */
    fun init(context: Context)
    
    /**
     * 销毁模块
     */
    fun destroy()
    
    /**
     * 连接服务
     */
    suspend fun connect(): Boolean
    
    /**
     * 断开连接
     */
    suspend fun disconnect()
    
    /**
     * 发送命令
     */
    suspend fun sendCommand(device: Device, property: Property, value: Any): CommandResult
    
    /**
     * 批量发送命令
     */
    suspend fun sendBatchCommands(commands: List<Command>): List<CommandResult>
    
    /**
     * 查询设备状态
     */
    suspend fun queryDeviceStatus(device: Device): DeviceStatusResult
    
    /**
     * 发现设备
     */
    suspend fun discoverDevices(): List<Device>
    
    /**
     * 添加监听器
     */
    fun addListener(listener: MiServiceListener)
    
    /**
     * 移除监听器
     */
    fun removeListener(listener: MiServiceListener)
    
    /**
     * 获取模块健康状态
     */
    fun getHealthStatus(): ModuleHealthStatus
}

/**
 * 命令定义
 */
data class Command(
    val device: Device,
    val property: Property,
    val value: Any,
    val priority: CommandPriority = CommandPriority.NORMAL,
    val retryCount: Int = 3,
    val timeout: Long = 5000L
)

/**
 * 命令优先级
 */
enum class CommandPriority {
    EMERGENCY,  // 紧急
    HIGH,       // 高
    NORMAL,     // 普通
    LOW,        // 低
    BATCH       // 批量
}

/**
 * 命令结果
 */
data class CommandResult(
    val success: Boolean,
    val deviceId: String,
    val propertyName: String,
    val errorCode: Int = 0,
    val errorMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val responseData: Any? = null
)

/**
 * 设备状态查询结果
 */
data class DeviceStatusResult(
    val success: Boolean,
    val device: Device? = null,
    val properties: Map<String, Property> = emptyMap(),
    val errorCode: Int = 0,
    val errorMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 模块健康状态
 */
data class ModuleHealthStatus(
    val isHealthy: Boolean,
    val lastHeartbeat: Long,
    val errorCount: Int,
    val responseTime: Long,
    val connectionQuality: ConnectionQuality,
    val message: String? = null
)

enum class ConnectionQuality {
    EXCELLENT,  // 优秀
    GOOD,       // 良好
    FAIR,       // 一般
    POOR,       // 较差
    UNKNOWN     // 未知
}
