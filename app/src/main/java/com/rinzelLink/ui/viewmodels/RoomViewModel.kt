package com.rinzelLink.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rinzelLink.core.room.Room
import com.rinzelLink.core.room.RoomType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()
    
    init {
        loadRooms()
    }
    
    private fun loadRooms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                rooms = getMockRooms()
            )
        }
    }
    
    fun navigateToRoom(roomId: String) {
        // 导航到房间详情页面
    }
    
    fun editRoom(roomId: String) {
        // 导航到编辑房间页面
    }
    
    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            val currentRooms = _uiState.value.rooms.toMutableList()
            currentRooms.removeAll { it.id == roomId }
            _uiState.value = _uiState.value.copy(rooms = currentRooms)
        }
    }
    
    fun addRoom() {
        // 导航到添加房间页面
    }
    
    private fun getMockRooms(): List<Room> {
        return listOf(
            Room(
                id = "living_room",
                name = "客厅",
                type = RoomType.LIVING_ROOM,
                description = "家庭活动中心",
                deviceCount = 5,
                color = 0xFF2196F3,
                isActive = true
            ),
            Room(
                id = "bedroom",
                name = "卧室",
                type = RoomType.BEDROOM,
                description = "休息空间",
                deviceCount = 3,
                color = 0xFF9C27B0,
                isActive = true
            ),
            Room(
                id = "kitchen",
                name = "厨房",
                type = RoomType.KITCHEN,
                description = "烹饪区域",
                deviceCount = 2,
                color = 0xFFFF9800,
                isActive = true
            ),
            Room(
                id = "bathroom",
                name = "卫生间",
                type = RoomType.BATHROOM,
                description = "洗漱空间",
                deviceCount = 1,
                color = 0xFF00BCD4,
                isActive = true
            ),
            Room(
                id = "study",
                name = "书房",
                type = RoomType.STUDY,
                description = "工作学习",
                deviceCount = 2,
                color = 0xFF795548,
                isActive = false
            ),
            Room(
                id = "balcony",
                name = "阳台",
                type = RoomType.BALCONY,
                description = "休闲空间",
                deviceCount = 0,
                color = 0xFF8BC34A,
                isActive = true
            )
        )
    }
}

data class RoomUiState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)