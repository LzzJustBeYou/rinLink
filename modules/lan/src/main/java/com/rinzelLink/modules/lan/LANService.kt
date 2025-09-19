package com.rinzelLink.modules.lan

import android.content.Context
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Protocol
import com.rinzelLink.core.device.Property
import com.rinzelLink.core.module.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 局域网服务模块
 * 负责局域网设备的发现、控制
 */
@Singleton
class LANService @Inject constructor() : MiServiceModule {
    
    override val name = "LAN Service"
    override val protocol = Protocol.LAN
    
    private val _isAvailable = MutableStateFlow(false)
    override val isAvailable: Boolean get() = _isAvailable.value
    
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: Boolean get() = _isConnected.value
    
    private val listeners = mutableSetOf<MiServiceListener>()
    private var context: Context? = null
    
    override fun init(context: Context) {
        this.context = context
        _isAvailable.value = true
    }
    
    override fun destroy() {
        listeners.clear()
        context = null
        _isAvailable.value = false
        _isConnected.value = false
    }
    
    override suspend fun connect(): Boolean {
        return try {
            kotlinx.coroutines.delay(1000)
            _isConnected.value = true
            notifyConnectionChanged(true)
            true
        } catch (e: Exception) {
            notifyModuleError(e)
            false
        }
    }
    
    override suspend fun disconnect() {
        _isConnected.value = false
        notifyConnectionChanged(false)
    }
    
    override suspend fun sendCommand(device: Device, property: Property, value: Any): CommandResult {
        return try {
            kotlinx.coroutines.delay(100)
            
            val result = CommandResult(
                success = true,
                deviceId = device.did,
                propertyName = property.name,
                responseData = value
            )
            
            val updatedProperty = property.copy(
                value = value,
                lastUpdate = System.currentTimeMillis()
            )
            notifyPropertyUpdate(device, updatedProperty)
            
            result
        } catch (e: Exception) {
            notifyModuleError(e)
            CommandResult(
                success = false,
                deviceId = device.did,
                propertyName = property.name,
                errorMessage = e.message
            )
        }
    }
    
    override suspend fun sendBatchCommands(commands: List<Command>): List<CommandResult> {
        val results = mutableListOf<CommandResult>()
        
        commands.forEach { command ->
            val result = sendCommand(command.device, command.property, command.value)
            results.add(result)
        }
        
        return results
    }
    
    override suspend fun queryDeviceStatus(device: Device): DeviceStatusResult {
        return try {
            kotlinx.coroutines.delay(200)
            
            val updatedDevice = device.copy(
                isOnline = true,
                lastSeen = System.currentTimeMillis()
            )
            
            DeviceStatusResult(
                success = true,
                device = updatedDevice,
                properties = device.properties
            )
        } catch (e: Exception) {
            notifyModuleError(e)
            DeviceStatusResult(
                success = false,
                errorMessage = e.message
            )
        }
    }
    
    override suspend fun discoverDevices(): List<Device> {
        return try {
            kotlinx.coroutines.delay(500)
            
            val mockDevices = listOf(
                Device(
                    did = "lan_light_001",
                    name = "客厅吊灯",
                    type = com.rinzelLink.core.device.DeviceType.LIGHT,
                    protocol = Protocol.LAN,
                    room = "living_room",
                    isOnline = true,
                    lastSeen = System.currentTimeMillis(),
                    properties = mapOf(
                        "power" to Property(
                            siid = 1,
                            piid = 1,
                            name = "power",
                            value = false,
                            type = com.rinzelLink.core.device.PropertyType.BOOLEAN,
                            writable = true
                        ),
                        "brightness" to Property(
                            siid = 1,
                            piid = 2,
                            name = "brightness",
                            value = 50,
                            type = com.rinzelLink.core.device.PropertyType.INTEGER,
                            writable = true,
                            range = com.rinzelLink.core.device.ValueRange(0, 100, 1)
                        )
                    )
                ),
                Device(
                    did = "lan_sensor_001",
                    name = "客厅温湿度传感器",
                    type = com.rinzelLink.core.device.DeviceType.SENSOR,
                    protocol = Protocol.LAN,
                    room = "living_room",
                    isOnline = true,
                    lastSeen = System.currentTimeMillis(),
                    properties = mapOf(
                        "temperature" to Property(
                            siid = 2,
                            piid = 1,
                            name = "temperature",
                            value = 22.5,
                            type = com.rinzelLink.core.device.PropertyType.FLOAT,
                            unit = "°C"
                        ),
                        "humidity" to Property(
                            siid = 2,
                            piid = 2,
                            name = "humidity",
                            value = 45.0,
                            type = com.rinzelLink.core.device.PropertyType.FLOAT,
                            unit = "%"
                        )
                    )
                )
            )
            
            mockDevices.forEach { device ->
                notifyDeviceDiscovered(device)
            }
            
            mockDevices
        } catch (e: Exception) {
            notifyModuleError(e)
            emptyList()
        }
    }
    
    override fun addListener(listener: MiServiceListener) {
        listeners.add(listener)
    }
    
    override fun removeListener(listener: MiServiceListener) {
        listeners.remove(listener)
    }
    
    override fun getHealthStatus(): ModuleHealthStatus {
        return ModuleHealthStatus(
            isHealthy = isConnected,
            lastHeartbeat = System.currentTimeMillis(),
            errorCount = 0,
            responseTime = 50L,
            connectionQuality = if (isConnected) ConnectionQuality.EXCELLENT else ConnectionQuality.UNKNOWN
        )
    }
    
    private fun notifyConnectionChanged(connected: Boolean) {
        listeners.forEach { it.onConnectionChanged(this, connected) }
    }
    
    private fun notifyPropertyUpdate(device: Device, property: Property) {
        listeners.forEach { it.onPropertyUpdate(device, property) }
    }
    
    private fun notifyDeviceDiscovered(device: Device) {
        listeners.forEach { it.onDeviceDiscovered(device) }
    }
    
    private fun notifyModuleError(error: Throwable) {
        listeners.forEach { it.onModuleError(this, error) }
    }
}
