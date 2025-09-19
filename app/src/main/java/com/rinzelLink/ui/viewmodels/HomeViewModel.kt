package com.rinzelLink.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.core.room.Room
import com.rinzelLink.core.room.RoomType
import com.rinzelLink.core.scene.Scene
import com.rinzelLink.core.scene.SceneService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sceneService: SceneService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            // 模拟加载数据
            _uiState.value = _uiState.value.copy(
                userName = "智能家居用户",
                weatherInfo = "晴天 25°C",
                quickControlDevices = getMockQuickControlDevices(),
                scenes = getMockScenes(),
                rooms = getMockRooms(),
                allDevices = getMockAllDevices()
            )
        }
    }
    
    fun toggleDevice(deviceId: String) {
        viewModelScope.launch {
            // 切换设备状态
            val currentDevices = _uiState.value.allDevices.toMutableList()
            val deviceIndex = currentDevices.indexOfFirst { it.did == deviceId }
            if (deviceIndex != -1) {
                val device = currentDevices[deviceIndex]
                currentDevices[deviceIndex] = device.copy(isOnline = !device.isOnline)
                _uiState.value = _uiState.value.copy(allDevices = currentDevices)
            }
        }
    }
    
    fun executeScene(sceneId: String) {
        viewModelScope.launch {
            // 执行场景
            val result = sceneService.executeScene(sceneId, _uiState.value.allDevices)
            // 处理执行结果
        }
    }
    
    fun navigateToRoom(roomId: String) {
        // 导航到房间详情页面
    }
    
    fun navigateToDevice(deviceId: String) {
        // 导航到设备详情页面
    }
    
    private fun getMockQuickControlDevices(): List<Device> {
        return listOf(
            Device(
                did = "light_001",
                name = "客厅主灯",
                type = DeviceType.LIGHT,
                protocol = com.rinzelLink.core.device.Protocol.ZIGBEE,
                room = "living_room",
                isOnline = true,
                properties = mapOf(
                    "power" to com.rinzelLink.core.device.Property("power", false),
                    "brightness" to com.rinzelLink.core.device.Property("brightness", 80)
                )
            ),
            Device(
                did = "switch_001",
                name = "客厅开关",
                type = DeviceType.SWITCH,
                protocol = com.rinzelLink.core.device.Protocol.ZIGBEE,
                room = "living_room",
                isOnline = true,
                properties = mapOf(
                    "power" to com.rinzelLink.core.device.Property("power", true)
                )
            ),
            Device(
                did = "sensor_001",
                name = "温度传感器",
                type = DeviceType.SENSOR,
                protocol = com.rinzelLink.core.device.Protocol.ZIGBEE,
                room = "living_room",
                isOnline = true,
                properties = mapOf(
                    "temperature" to com.rinzelLink.core.device.Property("temperature", 25.5)
                )
            )
        )
    }
    
    private fun getMockScenes(): List<Scene> {
        return listOf(
            Scene(
                id = "scene_001",
                name = "回家模式",
                description = "打开客厅灯光，调节空调温度",
                icon = "home",
                color = 0xFF4CAF50
            ),
            Scene(
                id = "scene_002",
                name = "睡眠模式",
                description = "关闭所有灯光，调节温度",
                icon = "bed",
                color = 0xFF9C27B0
            ),
            Scene(
                id = "scene_003",
                name = "观影模式",
                description = "调暗灯光，关闭窗帘",
                icon = "movie",
                color = 0xFF2196F3
            )
        )
    }
    
    private fun getMockRooms(): List<Room> {
        return listOf(
            Room(
                id = "living_room",
                name = "客厅",
                type = RoomType.LIVING_ROOM,
                deviceCount = 5,
                color = 0xFF2196F3
            ),
            Room(
                id = "bedroom",
                name = "卧室",
                type = RoomType.BEDROOM,
                deviceCount = 3,
                color = 0xFF9C27B0
            ),
            Room(
                id = "kitchen",
                name = "厨房",
                type = RoomType.KITCHEN,
                deviceCount = 2,
                color = 0xFFFF9800
            )
        )
    }
    
    private fun getMockAllDevices(): List<Device> {
        return listOf(
            Device(
                did = "light_001",
                name = "客厅主灯",
                type = DeviceType.LIGHT,
                protocol = com.rinzelLink.core.device.Protocol.ZIGBEE,
                room = "living_room",
                isOnline = true
            ),
            Device(
                did = "light_002",
                name = "卧室台灯",
                type = DeviceType.LIGHT,
                protocol = com.rinzelLink.core.device.Protocol.ZIGBEE,
                room = "bedroom",
                isOnline = false
            ),
            Device(
                did = "sensor_001",
                name = "温度传感器",
                type = DeviceType.SENSOR,
                protocol = com.rinzelLink.core.device.Protocol.ZIGBEE,
                room = "living_room",
                isOnline = true
            )
        )
    }
}

data class HomeUiState(
    val userName: String = "",
    val weatherInfo: String? = null,
    val quickControlDevices: List<Device> = emptyList(),
    val scenes: List<Scene> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val allDevices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
