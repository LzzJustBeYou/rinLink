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
import com.rinzelLink.ui.components.DeviceCard
import com.rinzelLink.ui.theme.*

@Composable
fun RoomDetailScreen(
    roomId: String,
    modifier: Modifier = Modifier,
    viewModel: RoomDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 房间头部信息
        item {
            RoomHeader(
                room = uiState.room,
                deviceCount = uiState.devices.size,
                onlineCount = uiState.devices.count { it.isOnline }
            )
        }
        
        // 快速控制
        item {
            QuickControlsSection(
                devices = uiState.devices.filter { it.isOnline },
                onDeviceToggle = viewModel::toggleDevice
            )
        }
        
        // 设备列表
        item {
            DevicesSection(
                devices = uiState.devices,
                onDeviceClick = viewModel::navigateToDevice,
                onDeviceToggle = viewModel::toggleDevice
            )
        }
    }
}

@Composable
private fun RoomHeader(
    room: Room?,
    deviceCount: Int,
    onlineCount: Int
) {
    if (room == null) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = room.color?.let { Color(it) } 
                ?: MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getRoomIcon(room.type),
                        contentDescription = room.name,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = room.description ?: "智能房间",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // 设备状态指示器
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$onlineCount/$deviceCount",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "设备在线",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 设备状态进度条
            LinearProgressIndicator(
                progress = if (deviceCount > 0) onlineCount.toFloat() / deviceCount else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (onlineCount == deviceCount) 
                    SuccessGreen 
                else 
                    WarningOrange,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun QuickControlsSection(
    devices: List<Device>,
    onDeviceToggle: (String) -> Unit
) {
    if (devices.isEmpty()) return
    
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
            .width(100.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getDeviceIcon(device.type),
                contentDescription = device.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = device.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun DevicesSection(
    devices: List<Device>,
    onDeviceClick: (String) -> Unit,
    onDeviceToggle: (String) -> Unit
) {
    Column {
        Text(
            text = "设备列表",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        if (devices.isEmpty()) {
            EmptyDevicesCard()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(devices) { device ->
                    DeviceCard(
                        device = device,
                        onToggle = { onDeviceToggle(device.did) },
                        onClick = { onDeviceClick(device.did) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDevicesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.DeviceHub,
                contentDescription = "无设备",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无设备",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "添加设备到房间中",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
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
