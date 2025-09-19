package com.rinzelLink.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.core.device.Protocol
import com.rinzelLink.core.device.Property
import com.rinzelLink.core.room.Room
import com.rinzelLink.core.room.RoomType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomDetailViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(RoomDetailUiState())
    val uiState: StateFlow<RoomDetailUiState> = _uiState.asStateFlow()
    
    fun loadRoom(roomId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                room = getMockRoom(roomId),
                devices = getMockDevices(roomId)
            )
        }
    }
    
    fun toggleDevice(deviceId: String) {
        viewModelScope.launch {
            val currentDevices = _uiState.value.devices.toMutableList()
            val deviceIndex = currentDevices.indexOfFirst { it.did == deviceId }
            if (deviceIndex != -1) {
                val device = currentDevices[deviceIndex]
                currentDevices[deviceIndex] = device.copy(isOnline = !device.isOnline)
                _uiState.value = _uiState.value.copy(devices = currentDevices)
            }
        }
    }
    
    fun navigateToDevice(deviceId: String) {
        // 导航到设备详情页面
    }
    
    private fun getMockRoom(roomId: String): Room? {
        return when (roomId) {
            "living_room" -> Room(
                id = "living_room",
                name = "客厅",
                type = RoomType.LIVING_ROOM,
                description = "家庭活动中心",
                deviceCount = 5,
                color = 0xFF2196F3
            )
            "bedroom" -> Room(
                id = "bedroom",
                name = "卧室",
                type = RoomType.BEDROOM,
                description = "休息空间",
                deviceCount = 3,
                color = 0xFF9C27B0
            )
            "kitchen" -> Room(
                id = "kitchen",
                name = "厨房",
                type = RoomType.KITCHEN,
                description = "烹饪区域",
                deviceCount = 2,
                color = 0xFFFF9800
            )
            else -> null
        }
    }
    
    private fun getMockDevices(roomId: String): List<Device> {
        return when (roomId) {
            "living_room" -> listOf(
                Device(
                    did = "light_001",
                    name = "客厅主灯",
                    type = DeviceType.LIGHT,
                    protocol = Protocol.ZIGBEE,
                    room = "living_room",
                    isOnline = true,
                    properties = mapOf(
                        "power" to Property("power", false),
                        "brightness" to Property("brightness", 80)
                    )
                ),
                Device(
                    did = "switch_001",
                    name = "客厅开关",
                    type = DeviceType.SWITCH,
                    protocol = Protocol.ZIGBEE,
                    room = "living_room",
                    isOnline = true,
                    properties = mapOf(
                        "power" to Property("power", true)
                    )
                ),
                Device(
                    did = "sensor_001",
                    name = "温度传感器",
                    type = DeviceType.SENSOR,
                    protocol = Protocol.ZIGBEE,
                    room = "living_room",
                    isOnline = true,
                    properties = mapOf(
                        "temperature" to Property("temperature", 25.5),
                        "humidity" to Property("humidity", 60.0)
                    )
                ),
                Device(
                    did = "curtain_001",
                    name = "客厅窗帘",
                    type = DeviceType.CURTAIN,
                    protocol = Protocol.ZIGBEE,
                    room = "living_room",
                    isOnline = false,
                    properties = mapOf(
                        "position" to Property("position", 50)
                    )
                )
            )
            "bedroom" -> listOf(
                Device(
                    did = "light_002",
                    name = "卧室台灯",
                    type = DeviceType.LIGHT,
                    protocol = Protocol.ZIGBEE,
                    room = "bedroom",
                    isOnline = true,
                    properties = mapOf(
                        "power" to Property("power", true),
                        "brightness" to Property("brightness", 30)
                    )
                ),
                Device(
                    did = "sensor_002",
                    name = "卧室传感器",
                    type = DeviceType.SENSOR,
                    protocol = Protocol.ZIGBEE,
                    room = "bedroom",
                    isOnline = true,
                    properties = mapOf(
                        "temperature" to Property("temperature", 23.0),
                        "motion" to Property("motion", false)
                    )
                )
            )
            "kitchen" -> listOf(
                Device(
                    did = "light_003",
                    name = "厨房灯",
                    type = DeviceType.LIGHT,
                    protocol = Protocol.ZIGBEE,
                    room = "kitchen",
                    isOnline = true,
                    properties = mapOf(
                        "power" to Property("power", false),
                        "brightness" to Property("brightness", 100)
                    )
                )
            )
            else -> emptyList()
        }
    }
}

data class RoomDetailUiState(
    val room: Room? = null,
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)