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
class DeviceViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()
    
    init {
        loadDevices()
    }
    
    private fun loadDevices() {
        viewModelScope.launch {
            val allDevices = getMockDevices()
            _uiState.value = _uiState.value.copy(
                allDevices = allDevices,
                filteredDevices = allDevices
            )
        }
    }
    
    fun selectDeviceType(deviceType: DeviceType?) {
        viewModelScope.launch {
            val filteredDevices = if (deviceType == null) {
                _uiState.value.allDevices
            } else {
                _uiState.value.allDevices.filter { it.type == deviceType }
            }
            
            _uiState.value = _uiState.value.copy(
                selectedDeviceType = deviceType,
                filteredDevices = filteredDevices
            )
        }
    }
    
    fun toggleDevice(deviceId: String) {
        viewModelScope.launch {
            val currentDevices = _uiState.value.allDevices.toMutableList()
            val deviceIndex = currentDevices.indexOfFirst { it.did == deviceId }
            if (deviceIndex != -1) {
                val device = currentDevices[deviceIndex]
                currentDevices[deviceIndex] = device.copy(isOnline = !device.isOnline)
                
                val filteredDevices = if (_uiState.value.selectedDeviceType == null) {
                    currentDevices
                } else {
                    currentDevices.filter { it.type == _uiState.value.selectedDeviceType }
                }
                
                _uiState.value = _uiState.value.copy(
                    allDevices = currentDevices,
                    filteredDevices = filteredDevices
                )
            }
        }
    }
    
    fun navigateToDevice(deviceId: String) {
        // 导航到设备详情页面
    }
    
    private fun getMockDevices(): List<Device> {
        return listOf(
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
                did = "light_002",
                name = "卧室台灯",
                type = DeviceType.LIGHT,
                protocol = Protocol.ZIGBEE,
                room = "bedroom",
                isOnline = false,
                properties = mapOf(
                    "power" to Property("power", true),
                    "brightness" to Property("brightness", 30)
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
            ),
            Device(
                did = "thermostat_001",
                name = "客厅温控器",
                type = DeviceType.THERMOSTAT,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                isOnline = true,
                properties = mapOf(
                    "temperature" to Property("temperature", 22.0),
                    "mode" to Property("mode", "auto")
                )
            ),
            Device(
                did = "camera_001",
                name = "门口摄像头",
                type = DeviceType.CAMERA,
                protocol = Protocol.WIFI,
                room = "entrance",
                isOnline = true,
                properties = mapOf(
                    "recording" to Property("recording", false)
                )
            )
        )
    }
}

data class DeviceUiState(
    val allDevices: List<Device> = emptyList(),
    val filteredDevices: List<Device> = emptyList(),
    val selectedDeviceType: DeviceType? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
