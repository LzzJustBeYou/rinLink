package com.rinzelLink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.core.room.Room
import com.rinzelLink.core.room.RoomType
import com.rinzelLink.core.scene.Scene
import com.rinzelLink.ui.components.*
import com.rinzelLink.ui.theme.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部欢迎区域
        item {
            WelcomeSection(
                userName = uiState.userName,
                weatherInfo = uiState.weatherInfo
            )
        }
        
        // 快速控制区域
        item {
            QuickControlSection(
                devices = uiState.quickControlDevices,
                onDeviceToggle = viewModel::toggleDevice
            )
        }
        
        // 场景区域
        item {
            SceneSection(
                scenes = uiState.scenes,
                onSceneClick = viewModel::executeScene
            )
        }
        
        // 房间区域
        item {
            RoomSection(
                rooms = uiState.rooms,
                onRoomClick = viewModel::navigateToRoom
            )
        }
        
        // 设备状态概览
        item {
            DeviceStatusSection(
                devices = uiState.allDevices,
                onDeviceClick = viewModel::navigateToDevice
            )
        }
    }
}

@Composable
private fun WelcomeSection(
    userName: String,
    weatherInfo: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "欢迎回来，$userName",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = weatherInfo ?: "今天是个好日子",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "智能家居",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun QuickControlSection(
    devices: List<Device>,
    onDeviceToggle: (String) -> Unit
) {
    Column {
        Text(
            text = "快速控制",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(devices) { device ->
                QuickControlCard(
                    device = device,
                    onToggle = { onDeviceToggle(device.did) }
                )
            }
        }
    }
}

@Composable
private fun QuickControlCard(
    device: Device,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isOnline) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getDeviceIcon(device.type),
                contentDescription = device.name,
                tint = if (device.isOnline) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = device.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun SceneSection(
    scenes: List<Scene>,
    onSceneClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "智能场景",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { /* 导航到场景页面 */ }) {
                Text("查看全部")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scenes) { scene ->
                SceneCard(
                    scene = scene,
                    onClick = { onSceneClick(scene.id) }
                )
            }
        }
    }
}

@Composable
private fun SceneCard(
    scene: Scene,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = scene.color?.let { Color(it) } 
                ?: MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = scene.name,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scene.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun RoomSection(
    rooms: List<Room>,
    onRoomClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "房间管理",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { /* 导航到房间页面 */ }) {
                Text("管理房间")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rooms) { room ->
                RoomCard(
                    room = room,
                    onClick = { onRoomClick(room.id) }
                )
            }
        }
    }
}

@Composable
private fun RoomCard(
    room: Room,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = room.color?.let { Color(it) } 
                ?: MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getRoomIcon(room.type),
                contentDescription = room.name,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = room.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                lineHeight = 14.sp
            )
            Text(
                text = "${room.deviceCount} 个设备",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DeviceStatusSection(
    devices: List<Device>,
    onDeviceClick: (String) -> Unit
) {
    Column {
        Text(
            text = "设备状态",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        val onlineCount = devices.count { it.isOnline }
        val totalCount = devices.size
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "设备概览",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$onlineCount/$totalCount 在线",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (onlineCount == totalCount) 
                            SuccessGreen 
                        else 
                            WarningOrange
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 设备状态进度条
                LinearProgressIndicator(
                    progress = if (totalCount > 0) onlineCount.toFloat() / totalCount else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (onlineCount == totalCount) 
                        SuccessGreen 
                    else 
                        WarningOrange,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }
    }
}

// 辅助函数
@Composable
private fun getDeviceIcon(deviceType: DeviceType): ImageVector {
    return when (deviceType) {
        DeviceType.LIGHT -> Icons.Default.Lightbulb
        DeviceType.SWITCH -> Icons.Default.Power
        DeviceType.SENSOR -> Icons.Default.Sensors
        DeviceType.THERMOSTAT -> Icons.Default.Thermostat
        DeviceType.CAMERA -> Icons.Default.CameraAlt
        DeviceType.DOOR_LOCK -> Icons.Default.Lock
        DeviceType.CURTAIN -> Icons.Default.Curtains
        DeviceType.AIR_CONDITIONER -> Icons.Default.Air
        DeviceType.FAN -> Icons.Default.Air
        DeviceType.SPEAKER -> Icons.Default.Speaker
        DeviceType.TV -> Icons.Default.Tv
        DeviceType.REFRIGERATOR -> Icons.Default.Kitchen
        DeviceType.WASHING_MACHINE -> Icons.Default.LocalLaundryService
        DeviceType.ROBOT_VACUUM -> Icons.Default.CleaningServices
        else -> Icons.Default.DeviceHub
    }
}

@Composable
private fun getRoomIcon(roomType: RoomType): ImageVector {
    return when (roomType) {
        RoomType.LIVING_ROOM -> Icons.Default.Living
        RoomType.BEDROOM -> Icons.Default.Bed
        RoomType.KITCHEN -> Icons.Default.Kitchen
        RoomType.BATHROOM -> Icons.Default.Bathroom
        RoomType.STUDY -> Icons.Default.MenuBook
        RoomType.DINING_ROOM -> Icons.Default.Dining
        RoomType.BALCONY -> Icons.Default.Balcony
        RoomType.GARAGE -> Icons.Default.Garage
        RoomType.GARDEN -> Icons.Default.Yard
        RoomType.OFFICE -> Icons.Default.Business
        RoomType.CORRIDOR -> Icons.Default.Corridor
        RoomType.STORAGE -> Icons.Default.Storage
        else -> Icons.Default.Room
    }
}
