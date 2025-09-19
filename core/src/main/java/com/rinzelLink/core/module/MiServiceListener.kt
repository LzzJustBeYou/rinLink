package com.rinzelLink.core.module

import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Property

/**
 * 服务模块事件监听器
 */
interface MiServiceListener {
    /**
     * 模块连接状态变化
     */
    fun onConnectionChanged(module: MiServiceModule, connected: Boolean)
    
    /**
     * 设备属性更新
     */
    fun onPropertyUpdate(device: Device, property: Property)
    
    /**
     * 设备状态变化
     */
    fun onDeviceStatusChanged(device: Device, isOnline: Boolean)
    
    /**
     * 设备发现
     */
    fun onDeviceDiscovered(device: Device)
    
    /**
     * 设备丢失
     */
    fun onDeviceLost(device: Device)
    
    /**
     * 命令执行结果
     */
    fun onCommandResult(result: CommandResult)
    
    /**
     * 模块异常
     */
    fun onModuleError(module: MiServiceModule, error: Throwable)
    
    /**
     * 模块健康状态变化
     */
    fun onHealthStatusChanged(module: MiServiceModule, status: ModuleHealthStatus)
}
