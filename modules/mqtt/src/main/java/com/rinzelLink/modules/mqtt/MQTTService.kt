package com.rinzelLink.modules.mqtt

import android.content.Context
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Protocol
import com.rinzelLink.core.device.Property
import com.rinzelLink.core.module.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MQTT服务模块
 * 负责MQTT协议设备的连接、控制、状态同步
 */
@Singleton
class MQTTService @Inject constructor() : MiServiceModule {
    
    override val name = "MQTT Service"
    override val protocol = Protocol.MQTT
    
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
            kotlinx.coroutines.delay(1800)
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
            kotlinx.coroutines.delay(250)
            
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
            kotlinx.coroutines.delay(600)
            
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
            kotlinx.coroutines.delay(1500)
            
            val mockDevices = listOf(
                Device(
                    did = "mqtt_thermostat_001",
                    name = "MQTT温控器",
                    type = com.rinzelLink.core.device.DeviceType.THERMOSTAT,
                    protocol = Protocol.MQTT,
                    room = "living_room",
                    isOnline = true,
                    lastSeen = System.currentTimeMillis(),
                    properties = mapOf(
                        "power" to Property(
                            siid = 1,
                            piid = 1,
                            name = "power",
                            value = true,
                            type = com.rinzelLink.core.device.PropertyType.BOOLEAN,
                            writable = true
                        ),
                        "target_temp" to Property(
                            siid = 1,
                            piid = 2,
                            name = "target_temp",
                            value = 22.0,
                            type = com.rinzelLink.core.device.PropertyType.FLOAT,
                            writable = true,
                            range = com.rinzelLink.core.device.ValueRange(16.0, 30.0, 0.5),
                            unit = "°C"
                        ),
                        "current_temp" to Property(
                            siid = 1,
                            piid = 3,
                            name = "current_temp",
                            value = 21.5,
                            type = com.rinzelLink.core.device.PropertyType.FLOAT,
                            unit = "°C"
                        ),
                        "mode" to Property(
                            siid = 1,
                            piid = 4,
                            name = "mode",
                            value = "auto",
                            type = com.rinzelLink.core.device.PropertyType.ENUM,
                            writable = true,
                            range = com.rinzelLink.core.device.ValueRange(
                                enumValues = listOf("auto", "heat", "cool", "fan", "off")
                            )
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
            responseTime = 300L,
            connectionQuality = if (isConnected) ConnectionQuality.FAIR else ConnectionQuality.UNKNOWN
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