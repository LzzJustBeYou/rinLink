package com.rinzelLink.core.module

import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Property
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 状态缓存管理器
 * 缓存设备状态，支持历史状态查询
 */
@Singleton
class StateCache @Inject constructor() {
    
    private val deviceCache = mutableMapOf<String, Device>()
    private val propertyHistory = mutableMapOf<String, MutableList<PropertySnapshot>>()
    
    private val _deviceStatusFlow = MutableSharedFlow<Device>()
    val deviceStatusFlow: SharedFlow<Device> = _deviceStatusFlow.asSharedFlow()
    
    /**
     * 更新设备
     */
    fun updateDevice(device: Device) {
        deviceCache[device.did] = device
        _deviceStatusFlow.tryEmit(device)
    }
    
    /**
     * 更新设备属性
     */
    fun updateDeviceProperty(deviceId: String, propertyName: String, value: Any) {
        deviceCache[deviceId]?.let { device ->
            val updatedProperties = device.properties.toMutableMap()
            updatedProperties[propertyName]?.let { property ->
                val updatedProperty = property.copy(
                    value = value,
                    lastUpdate = System.currentTimeMillis()
                )
                updatedProperties[propertyName] = updatedProperty
                
                val updatedDevice = device.copy(properties = updatedProperties)
                deviceCache[deviceId] = updatedDevice
                
                // 记录历史
                recordPropertyHistory(deviceId, propertyName, value)
                
                _deviceStatusFlow.tryEmit(updatedDevice)
            }
        }
    }
    
    /**
     * 更新设备在线状态
     */
    fun updateDeviceOnlineStatus(deviceId: String, isOnline: Boolean) {
        deviceCache[deviceId]?.let { device ->
            val updatedDevice = device.copy(
                isOnline = isOnline,
                lastSeen = if (isOnline) System.currentTimeMillis() else device.lastSeen
            )
            deviceCache[deviceId] = updatedDevice
            _deviceStatusFlow.tryEmit(updatedDevice)
        }
    }
    
    /**
     * 获取设备
     */
    fun getDevice(deviceId: String): Device? {
        return deviceCache[deviceId]
    }
    
    /**
     * 获取所有设备
     */
    fun getAllDevices(): List<Device> {
        return deviceCache.values.toList()
    }
    
    /**
     * 获取设备状态流
     */
    fun getDeviceStatusFlow(): Flow<Device> {
        return deviceStatusFlow
    }
    
    /**
     * 按房间获取设备
     */
    fun getDevicesByRoom(roomId: String): List<Device> {
        return deviceCache.values.filter { it.room == roomId }
    }
    
    /**
     * 按类型获取设备
     */
    fun getDevicesByType(type: com.rinzelLink.core.device.DeviceType): List<Device> {
        return deviceCache.values.filter { it.type == type }
    }
    
    /**
     * 获取在线设备
     */
    fun getOnlineDevices(): List<Device> {
        return deviceCache.values.filter { it.isOnline }
    }
    
    /**
     * 记录属性历史
     */
    private fun recordPropertyHistory(deviceId: String, propertyName: String, value: Any) {
        val key = "$deviceId:$propertyName"
        val history = propertyHistory.getOrPut(key) { mutableListOf() }
        
        val snapshot = PropertySnapshot(
            value = value,
            timestamp = System.currentTimeMillis()
        )
        
        history.add(snapshot)
        
        // 限制历史记录数量
        if (history.size > 1000) {
            history.removeAt(0)
        }
    }
    
    /**
     * 获取属性历史
     */
    fun getPropertyHistory(deviceId: String, propertyName: String, limit: Int = 100): List<PropertySnapshot> {
        val key = "$deviceId:$propertyName"
        return propertyHistory[key]?.takeLast(limit) ?: emptyList()
    }
    
    /**
     * 清空缓存
     */
    fun clear() {
        deviceCache.clear()
        propertyHistory.clear()
    }
}

/**
 * 属性快照
 */
data class PropertySnapshot(
    val value: Any,
    val timestamp: Long
)
