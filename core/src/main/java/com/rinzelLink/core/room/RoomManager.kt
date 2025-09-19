package com.rinzelLink.core.room

import com.rinzelLink.core.device.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 房间管理器
 * 负责房间/区域管理，设备移动和统计
 */
@Singleton
class RoomManager @Inject constructor() {
    
    private val rooms = mutableMapOf<String, Room>()
    private val _roomsFlow = MutableSharedFlow<List<Room>>()
    val roomsFlow: SharedFlow<List<Room>> = _roomsFlow.asSharedFlow()
    
    init {
        // 初始化默认房间
        initializeDefaultRooms()
    }
    
    /**
     * 初始化默认房间
     */
    private fun initializeDefaultRooms() {
        val defaultRooms = listOf(
            Room(
                id = "living_room",
                name = "客厅",
                type = RoomType.LIVING_ROOM,
                icon = "ic_living_room",
                color = 0xFF4CAF50.toInt()
            ),
            Room(
                id = "bedroom",
                name = "卧室",
                type = RoomType.BEDROOM,
                icon = "ic_bedroom",
                color = 0xFF2196F3.toInt()
            ),
            Room(
                id = "kitchen",
                name = "厨房",
                type = RoomType.KITCHEN,
                icon = "ic_kitchen",
                color = 0xFFFF9800.toInt()
            ),
            Room(
                id = "bathroom",
                name = "卫生间",
                type = RoomType.BATHROOM,
                icon = "ic_bathroom",
                color = 0xFF9C27B0.toInt()
            ),
            Room(
                id = "study",
                name = "书房",
                type = RoomType.STUDY,
                icon = "ic_study",
                color = 0xFF607D8B.toInt()
            )
        )
        
        defaultRooms.forEach { room ->
            rooms[room.id] = room
        }
        
        _roomsFlow.tryEmit(rooms.values.toList())
    }
    
    /**
     * 获取所有房间
     */
    fun getAllRooms(): List<Room> {
        return rooms.values.toList()
    }
    
    /**
     * 根据ID获取房间
     */
    fun getRoomById(roomId: String): Room? {
        return rooms[roomId]
    }
    
    /**
     * 添加房间
     */
    fun addRoom(room: Room) {
        rooms[room.id] = room
        _roomsFlow.tryEmit(rooms.values.toList())
    }
    
    /**
     * 更新房间
     */
    fun updateRoom(room: Room) {
        rooms[room.id] = room
        _roomsFlow.tryEmit(rooms.values.toList())
    }
    
    /**
     * 删除房间
     */
    fun deleteRoom(roomId: String) {
        rooms.remove(roomId)
        _roomsFlow.tryEmit(rooms.values.toList())
    }
    
    /**
     * 移动设备到房间
     */
    fun moveDeviceToRoom(device: Device, roomId: String): Device? {
        val room = rooms[roomId] ?: return null
        
        val updatedDevice = device.copy(room = roomId)
        
        // 更新房间设备数量
        val updatedRoom = room.copy(deviceCount = room.deviceCount + 1)
        rooms[roomId] = updatedRoom
        _roomsFlow.tryEmit(rooms.values.toList())
        
        return updatedDevice
    }
    
    /**
     * 从房间移除设备
     */
    fun removeDeviceFromRoom(device: Device): Device? {
        val currentRoomId = device.room ?: return device
        
        val updatedDevice = device.copy(room = null)
        
        // 更新房间设备数量
        rooms[currentRoomId]?.let { room ->
            val updatedRoom = room.copy(deviceCount = maxOf(0, room.deviceCount - 1))
            rooms[currentRoomId] = updatedRoom
            _roomsFlow.tryEmit(rooms.values.toList())
        }
        
        return updatedDevice
    }
    
    /**
     * 获取房间设备统计
     */
    fun getRoomDeviceStats(devices: List<Device>): Map<String, RoomDeviceStats> {
        val stats = mutableMapOf<String, RoomDeviceStats>()
        
        devices.forEach { device ->
            val roomId = device.room ?: "unassigned"
            val currentStats = stats.getOrPut(roomId) { 
                RoomDeviceStats(roomId, 0, 0, emptyList()) 
            }
            
            val updatedStats = currentStats.copy(
                totalDevices = currentStats.totalDevices + 1,
                onlineDevices = currentStats.onlineDevices + if (device.isOnline) 1 else 0,
                deviceTypes = currentStats.deviceTypes + device.type
            )
            
            stats[roomId] = updatedStats
        }
        
        return stats
    }
}

/**
 * 房间设备统计
 */
data class RoomDeviceStats(
    val roomId: String,
    val totalDevices: Int,
    val onlineDevices: Int,
    val deviceTypes: List<com.rinzelLink.core.device.DeviceType>
)
