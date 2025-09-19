package com.rinzelLink.core.group

import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.core.room.DeviceGroup
import com.rinzelLink.core.room.GroupType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设备分组管理器
 * 负责多设备分组控制，支持类型/房间/自定义组合
 */
@Singleton
class DeviceGroupManager @Inject constructor() {
    
    private val groups = mutableMapOf<String, DeviceGroup>()
    private val _groupsFlow = MutableSharedFlow<List<DeviceGroup>>()
    val groupsFlow: SharedFlow<List<DeviceGroup>> = _groupsFlow.asSharedFlow()
    
    /**
     * 获取所有分组
     */
    fun getAllGroups(): List<DeviceGroup> {
        return groups.values.toList()
    }
    
    /**
     * 根据ID获取分组
     */
    fun getGroupById(groupId: String): DeviceGroup? {
        return groups[groupId]
    }
    
    /**
     * 添加分组
     */
    fun addGroup(group: DeviceGroup) {
        groups[group.id] = group
        _groupsFlow.tryEmit(groups.values.toList())
    }
    
    /**
     * 更新分组
     */
    fun updateGroup(group: DeviceGroup) {
        groups[group.id] = group
        _groupsFlow.tryEmit(groups.values.toList())
    }
    
    /**
     * 删除分组
     */
    fun deleteGroup(groupId: String) {
        groups.remove(groupId)
        _groupsFlow.tryEmit(groups.values.toList())
    }
    
    /**
     * 创建按房间分组
     */
    fun createRoomGroups(devices: List<Device>): List<DeviceGroup> {
        val roomGroups = devices
            .groupBy { it.room ?: "unassigned" }
            .map { (roomId, roomDevices) ->
                DeviceGroup(
                    id = "room_$roomId",
                    name = "房间: ${roomId}",
                    type = GroupType.BY_ROOM,
                    deviceIds = roomDevices.map { it.did },
                    roomIds = listOf(roomId)
                )
            }
        
        roomGroups.forEach { group ->
            groups[group.id] = group
        }
        
        _groupsFlow.tryEmit(groups.values.toList())
        return roomGroups
    }
    
    /**
     * 创建按类型分组
     */
    fun createTypeGroups(devices: List<Device>): List<DeviceGroup> {
        val typeGroups = devices
            .groupBy { it.type }
            .map { (deviceType, typeDevices) ->
                DeviceGroup(
                    id = "type_${deviceType.name.lowercase()}",
                    name = "类型: ${getDeviceTypeDisplayName(deviceType)}",
                    type = GroupType.BY_TYPE,
                    deviceIds = typeDevices.map { it.did }
                )
            }
        
        typeGroups.forEach { group ->
            groups[group.id] = group
        }
        
        _groupsFlow.tryEmit(groups.values.toList())
        return typeGroups
    }
    
    /**
     * 创建按能力分组
     */
    fun createCapabilityGroups(devices: List<Device>): List<DeviceGroup> {
        val capabilityGroups = mutableListOf<DeviceGroup>()
        
        // 按能力分组
        val capabilityMap = mutableMapOf<String, MutableList<Device>>()
        
        devices.forEach { device ->
            device.capabilities.forEach { capability ->
                capabilityMap.getOrPut(capability.name) { mutableListOf() }.add(device)
            }
        }
        
        capabilityMap.forEach { (capability, capabilityDevices) ->
            val group = DeviceGroup(
                id = "capability_${capability.lowercase()}",
                name = "能力: ${getCapabilityDisplayName(capability)}",
                type = GroupType.BY_CAPABILITY,
                deviceIds = capabilityDevices.map { it.did }
            )
            capabilityGroups.add(group)
            groups[group.id] = group
        }
        
        _groupsFlow.tryEmit(groups.values.toList())
        return capabilityGroups
    }
    
    /**
     * 创建智能分组
     */
    fun createSmartGroups(devices: List<Device>): List<DeviceGroup> {
        val smartGroups = mutableListOf<DeviceGroup>()
        
        // 智能分组逻辑：根据使用频率、房间位置、设备类型等
        val frequentlyUsedDevices = devices.filter { device ->
            // 这里可以根据历史使用数据判断
            device.isOnline
        }
        
        if (frequentlyUsedDevices.isNotEmpty()) {
            val group = DeviceGroup(
                id = "smart_frequent",
                name = "常用设备",
                type = GroupType.SMART,
                deviceIds = frequentlyUsedDevices.map { it.did }
            )
            smartGroups.add(group)
            groups[group.id] = group
        }
        
        _groupsFlow.tryEmit(groups.values.toList())
        return smartGroups
    }
    
    /**
     * 获取分组中的设备
     */
    fun getGroupDevices(groupId: String, allDevices: List<Device>): List<Device> {
        val group = groups[groupId] ?: return emptyList()
        return allDevices.filter { device -> device.did in group.deviceIds }
    }
    
    /**
     * 添加设备到分组
     */
    fun addDeviceToGroup(groupId: String, deviceId: String) {
        groups[groupId]?.let { group ->
            val updatedGroup = group.copy(
                deviceIds = group.deviceIds + deviceId
            )
            groups[groupId] = updatedGroup
            _groupsFlow.tryEmit(groups.values.toList())
        }
    }
    
    /**
     * 从分组移除设备
     */
    fun removeDeviceFromGroup(groupId: String, deviceId: String) {
        groups[groupId]?.let { group ->
            val updatedGroup = group.copy(
                deviceIds = group.deviceIds - deviceId
            )
            groups[groupId] = updatedGroup
            _groupsFlow.tryEmit(groups.values.toList())
        }
    }
    
    private fun getDeviceTypeDisplayName(deviceType: DeviceType): String {
        return when (deviceType) {
            DeviceType.LIGHT -> "灯具"
            DeviceType.SWITCH -> "开关"
            DeviceType.SENSOR -> "传感器"
            DeviceType.THERMOSTAT -> "温控器"
            DeviceType.CAMERA -> "摄像头"
            DeviceType.DOOR_LOCK -> "门锁"
            DeviceType.CURTAIN -> "窗帘"
            DeviceType.AIR_CONDITIONER -> "空调"
            DeviceType.FAN -> "风扇"
            DeviceType.SPEAKER -> "音响"
            DeviceType.TV -> "电视"
            DeviceType.REFRIGERATOR -> "冰箱"
            DeviceType.WASHING_MACHINE -> "洗衣机"
            DeviceType.ROBOT_VACUUM -> "扫地机器人"
            DeviceType.OTHER -> "其他"
        }
    }
    
    private fun getCapabilityDisplayName(capability: String): String {
        return when (capability) {
            "ON_OFF" -> "开关控制"
            "DIMMING" -> "调光"
            "COLOR" -> "颜色控制"
            "TEMPERATURE" -> "温度控制"
            "HUMIDITY" -> "湿度控制"
            "MOTION" -> "运动检测"
            "LIGHT_SENSOR" -> "光线传感器"
            "SOUND" -> "声音控制"
            "SCHEDULE" -> "定时功能"
            "SCENE" -> "场景支持"
            "GROUP" -> "分组控制"
            "REMOTE" -> "远程控制"
            "VOICE" -> "语音控制"
            "ENERGY_MONITOR" -> "能耗监控"
            else -> capability
        }
    }
}
