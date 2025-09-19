package com.rinzelLink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.rinzelLink.core.room.Room
import com.rinzelLink.core.room.RoomType
import com.rinzelLink.ui.theme.*

@Composable
fun RoomScreen(
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部统计
        item {
            RoomStatsSection(
                totalRooms = uiState.rooms.size,
                activeRooms = uiState.rooms.count { it.isActive },
                totalDevices = uiState.rooms.sumOf { it.deviceCount }
            )
        }
        
        // 房间网格
        item {
            RoomGridSection(
                rooms = uiState.rooms,
                onRoomClick = viewModel::navigateToRoom,
                onEditRoom = viewModel::editRoom,
                onDeleteRoom = viewModel::deleteRoom
            )
        }
        
        // 添加房间按钮
        item {
            AddRoomCard(
                onClick = viewModel::addRoom
            )
        }
    }
}

@Composable
private fun RoomStatsSection(
    totalRooms: Int,
    activeRooms: Int,
    totalDevices: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "房间概览",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "总房间",
                    value = totalRooms.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "活跃房间",
                    value = activeRooms.toString(),
                    color = SuccessGreen
                )
                StatItem(
                    label = "总设备",
                    value = totalDevices.toString(),
                    color = AqaraBlue
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun RoomGridSection(
    rooms: List<Room>,
    onRoomClick: (String) -> Unit,
    onEditRoom: (String) -> Unit,
    onDeleteRoom: (String) -> Unit
) {
    Column {
        Text(
            text = "房间列表",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rooms) { room ->
                RoomGridCard(
                    room = room,
                    onClick = { onRoomClick(room.id) },
                    onEdit = { onEditRoom(room.id) },
                    onDelete = { onDeleteRoom(room.id) }
                )
            }
        }
    }
}

@Composable
private fun RoomGridCard(
    room: Room,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = room.color?.let { Color(it) } 
                ?: MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = getRoomIcon(room.type),
                        contentDescription = room.name,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    // 操作按钮
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Column {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = room.description ?: "智能房间",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${room.deviceCount} 个设备",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddRoomCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加房间",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "添加新房间",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "创建您的智能房间",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// 辅助函数
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
