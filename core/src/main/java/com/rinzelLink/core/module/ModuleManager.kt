package com.rinzelLink.core.module

import android.content.Context
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Protocol
import com.rinzelLink.core.room.Room
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 模块管理器
 * 统一管理所有服务模块，负责协议路由、消息队列、状态缓存
 */
@Singleton
class ModuleManager @Inject constructor() {
    
    private val modules = mutableMapOf<Protocol, MiServiceModule>()
    private val listeners = mutableSetOf<MiServiceListener>()
    private val commandQueue = CommandQueue()
    private val stateCache = StateCache()
    
    /**
     * 注册模块
     */
    fun registerModule(module: MiServiceModule) {
        modules[module.protocol] = module
        module.addListener(createModuleListener())
    }
    
    /**
     * 注销模块
     */
    fun unregisterModule(protocol: Protocol) {
        modules[protocol]?.let { module ->
            module.removeListener(createModuleListener())
            module.destroy()
            modules.remove(protocol)
        }
    }
    
    /**
     * 初始化所有模块
     */
    suspend fun initializeAll(context: Context) {
        modules.values.forEach { module ->
            module.init(context)
            module.connect()
        }
    }
    
    /**
     * 销毁所有模块
     */
    suspend fun destroyAll() {
        modules.values.forEach { module ->
            module.disconnect()
            module.destroy()
        }
        modules.clear()
    }
    
    /**
     * 发送命令（自动选择最优协议）
     */
    suspend fun sendCommand(device: Device, property: com.rinzelLink.core.device.Property, value: Any): CommandResult {
        val command = Command(device, property, value)
        return sendCommand(command)
    }
    
    /**
     * 发送命令
     */
    suspend fun sendCommand(command: Command): CommandResult {
        // 添加到队列
        commandQueue.enqueue(command)
        
        // 选择最优模块
        val module = selectOptimalModule(command.device)
        return if (module != null) {
            try {
                val result = module.sendCommand(command.device, command.property, command.value)
                if (result.success) {
                    // 更新状态缓存
                    stateCache.updateDeviceProperty(command.device.did, command.property.name, command.value)
                }
                result
            } catch (e: Exception) {
                CommandResult(false, command.device.did, command.property.name, -1, e.message)
            }
        } else {
            CommandResult(false, command.device.did, command.property.name, -1, "No available module")
        }
    }
    
    /**
     * 批量发送命令
     */
    suspend fun sendBatchCommands(commands: List<Command>): List<CommandResult> {
        val results = mutableListOf<CommandResult>()
        
        // 按模块分组
        val commandsByModule = commands.groupBy { selectOptimalModule(it.device) }
        
        commandsByModule.forEach { (module, moduleCommands) ->
            if (module != null) {
                try {
                    val moduleResults = module.sendBatchCommands(moduleCommands)
                    results.addAll(moduleResults)
                } catch (e: Exception) {
                    // 单个失败，添加到结果中
                    moduleCommands.forEach { command ->
                        results.add(CommandResult(false, command.device.did, command.property.name, -1, e.message))
                    }
                }
            } else {
                // 没有可用模块
                moduleCommands.forEach { command ->
                    results.add(CommandResult(false, command.device.did, command.property.name, -1, "No available module"))
                }
            }
        }
        
        return results
    }
    
    /**
     * 查询设备状态
     */
    suspend fun queryDeviceStatus(device: Device): DeviceStatusResult {
        val module = selectOptimalModule(device)
        return if (module != null) {
            try {
                val result = module.queryDeviceStatus(device)
                if (result.success && result.device != null) {
                    // 更新状态缓存
                    stateCache.updateDevice(result.device)
                }
                result
            } catch (e: Exception) {
                DeviceStatusResult(false, errorMessage = e.message)
            }
        } else {
            DeviceStatusResult(false, errorMessage = "No available module")
        }
    }
    
    /**
     * 发现设备
     */
    suspend fun discoverDevices(): List<Device> {
        val allDevices = mutableListOf<Device>()
        
        modules.values.forEach { module ->
            try {
                val devices = module.discoverDevices()
                allDevices.addAll(devices)
                
                // 更新状态缓存
                devices.forEach { device ->
                    stateCache.updateDevice(device)
                }
            } catch (e: Exception) {
                // 记录错误但不中断其他模块的发现
            }
        }
        
        return allDevices
    }
    
    /**
     * 获取所有设备
     */
    fun getAllDevices(): List<Device> {
        return stateCache.getAllDevices()
    }
    
    /**
     * 获取设备状态流
     */
    fun getDeviceStatusFlow(): Flow<Device> {
        return stateCache.getDeviceStatusFlow()
    }
    
    /**
     * 添加监听器
     */
    fun addListener(listener: MiServiceListener) {
        listeners.add(listener)
    }
    
    /**
     * 移除监听器
     */
    fun removeListener(listener: MiServiceListener) {
        listeners.remove(listener)
    }
    
    /**
     * 选择最优模块
     */
    private fun selectOptimalModule(device: Device): MiServiceModule? {
        // 优先级：LAN > Zigbee > WebSocket > BLE > MQTT
        val priorityOrder = listOf(
            Protocol.LAN,
            Protocol.ZIGBEE,
            Protocol.WEBSOCKET,
            Protocol.BLE,
            Protocol.MQTT
        )
        
        for (protocol in priorityOrder) {
            val module = modules[protocol]
            if (module != null && module.isAvailable && module.isConnected) {
                return module
            }
        }
        
        return null
    }
    
    /**
     * 创建模块监听器
     */
    private fun createModuleListener(): MiServiceListener {
        return object : MiServiceListener {
            override fun onConnectionChanged(module: MiServiceModule, connected: Boolean) {
                listeners.forEach { it.onConnectionChanged(module, connected) }
            }
            
            override fun onPropertyUpdate(device: Device, property: com.rinzelLink.core.device.Property) {
                stateCache.updateDeviceProperty(device.did, property.name, property.value)
                listeners.forEach { it.onPropertyUpdate(device, property) }
            }
            
            override fun onDeviceStatusChanged(device: Device, isOnline: Boolean) {
                stateCache.updateDeviceOnlineStatus(device.did, isOnline)
                listeners.forEach { it.onDeviceStatusChanged(device, isOnline) }
            }
            
            override fun onDeviceDiscovered(device: Device) {
                stateCache.updateDevice(device)
                listeners.forEach { it.onDeviceDiscovered(device) }
            }
            
            override fun onDeviceLost(device: Device) {
                stateCache.updateDeviceOnlineStatus(device.did, false)
                listeners.forEach { it.onDeviceLost(device) }
            }
            
            override fun onCommandResult(result: CommandResult) {
                listeners.forEach { it.onCommandResult(result) }
            }
            
            override fun onModuleError(module: MiServiceModule, error: Throwable) {
                listeners.forEach { it.onModuleError(module, error) }
            }
            
            override fun onHealthStatusChanged(module: MiServiceModule, status: ModuleHealthStatus) {
                listeners.forEach { it.onHealthStatusChanged(module, status) }
            }
        }
    }
}
