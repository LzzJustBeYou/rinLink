package com.rinzelLink.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.core.device.Protocol
import com.rinzelLink.core.device.Property
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()
    
    fun loadDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                device = getMockDevice(deviceId),
                deviceHistory = getMockDeviceHistory(deviceId)
            )
        }
    }
    
    fun toggleDeviceOnline() {
        viewModelScope.launch {
            val currentDevice = _uiState.value.device ?: return@launch
            val updatedDevice = currentDevice.copy(isOnline = !currentDevice.isOnline)
            _uiState.value = _uiState.value.copy(device = updatedDevice)
            
            // 添加历史记录
            addHistoryItem(
                if (updatedDevice.isOnline) "online" else "offline",
                if (updatedDevice.isOnline) "设备上线" else "设备离线"
            )
        }
    }
    
    fun updateDeviceProperty(propertyName: String, value: Any) {
        viewModelScope.launch {
            val currentDevice = _uiState.value.device ?: return@launch
            val updatedProperties = currentDevice.properties.toMutableMap()
            updatedProperties[propertyName] = Property(propertyName, value)
            
            val updatedDevice = currentDevice.copy(properties = updatedProperties)
            _uiState.value = _uiState.value.copy(device = updatedDevice)
            
            // 添加历史记录
            addHistoryItem(
                propertyName,
                "更新 $propertyName 为 $value"
            )
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deviceHistory = emptyList())
        }
    }
    
    private fun addHistoryItem(type: String, description: String) {
        val newItem = DeviceHistoryItem(
            type = type,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        val currentHistory = _uiState.value.deviceHistory.toMutableList()
        currentHistory.add(0, newItem) // 添加到开头
        if (currentHistory.size > 50) { // 限制历史记录数量
            currentHistory.removeAt(currentHistory.size - 1)
        }
        _uiState.value = _uiState.value.copy(deviceHistory = currentHistory)
    }
    
    private fun getMockDevice(deviceId: String): Device? {
        return when (deviceId) {
            "light_001" -> Device(
                did = "light_001",
                name = "客厅主灯",
                type = DeviceType.LIGHT,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                model = "Aqara LED Bulb",
                manufacturer = "绿米",
                isOnline = true,
                lastSeen = System.currentTimeMillis() - 300000, // 5分钟前
                properties = mapOf(
                    "power" to Property("power", false),
                    "brightness" to Property("brightness", 80),
                    "color_temp" to Property("color_temp", 4000)
                ),
                tags = listOf("客厅", "照明"),
                capabilities = listOf(
                    com.rinzelLink.core.device.Capability.ON_OFF,
                    com.rinzelLink.core.device.Capability.DIMMING,
                    com.rinzelLink.core.device.Capability.COLOR
                )
            )
            "sensor_001" -> Device(
                did = "sensor_001",
                name = "温度传感器",
                type = DeviceType.SENSOR,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                model = "Aqara Temperature Sensor",
                manufacturer = "绿米",
                isOnline = true,
                lastSeen = System.currentTimeMillis() - 60000, // 1分钟前
                properties = mapOf(
                    "temperature" to Property("temperature", 25.5),
                    "humidity" to Property("humidity", 60.0),
                    "pressure" to Property("pressure", 1013.25)
                ),
                tags = listOf("客厅", "传感器"),
                capabilities = listOf(
                    com.rinzelLink.core.device.Capability.TEMPERATURE,
                    com.rinzelLink.core.device.Capability.HUMIDITY
                )
            )
            "thermostat_001" -> Device(
                did = "thermostat_001",
                name = "客厅温控器",
                type = DeviceType.THERMOSTAT,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                model = "Aqara Thermostat",
                manufacturer = "绿米",
                isOnline = true,
                lastSeen = System.currentTimeMillis() - 120000, // 2分钟前
                properties = mapOf(
                    "temperature" to Property("temperature", 22.0),
                    "mode" to Property("mode", "auto"),
                    "target_temp" to Property("target_temp", 23.0)
                ),
                tags = listOf("客厅", "温控"),
                capabilities = listOf(
                    com.rinzelLink.core.device.Capability.TEMPERATURE,
                    com.rinzelLink.core.device.Capability.SCHEDULE
                )
            )
            else -> null
        }
    }
    
    private fun getMockDeviceHistory(deviceId: String): List<DeviceHistoryItem> {
        val now = System.currentTimeMillis()
        return listOf(
            DeviceHistoryItem("power", "开启设备", now - 300000),
            DeviceHistoryItem("brightness", "调整亮度到 80%", now - 600000),
            DeviceHistoryItem("online", "设备上线", now - 900000),
            DeviceHistoryItem("brightness", "调整亮度到 60%", now - 1200000),
            DeviceHistoryItem("power", "关闭设备", now - 1800000)
        )
    }
}

data class DeviceDetailUiState(
    val device: Device? = null,
    val deviceHistory: List<DeviceHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DeviceHistoryItem(
    val type: String,
    val description: String,
    val timestamp: Long
)
