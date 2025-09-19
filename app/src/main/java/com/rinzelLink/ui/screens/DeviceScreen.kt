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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.ui.components.DeviceCard
import com.rinzelLink.ui.theme.*

@Composable
fun DeviceScreen(
    modifier: Modifier = Modifier,
    viewModel: DeviceViewModel = hiltViewModel()
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
            DeviceStatsSection(
                totalDevices = uiState.allDevices.size,
                onlineDevices = uiState.allDevices.count { it.isOnline },
                offlineDevices = uiState.allDevices.count { !it.isOnline }
            )
        }
        
        // 设备类型筛选
        item {
            DeviceTypeFilterSection(
                selectedType = uiState.selectedDeviceType,
                onTypeSelected = viewModel::selectDeviceType
            )
        }
        
        // 设备列表
        item {
            DeviceListSection(
                devices = uiState.filteredDevices,
                onDeviceClick = viewModel::navigateToDevice,
                onDeviceToggle = viewModel::toggleDevice
            )
        }
    }
}

@Composable
private fun DeviceStatsSection(
    totalDevices: Int,
    onlineDevices: Int,
    offlineDevices: Int
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
                text = "设备概览",
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
                    label = "总设备",
                    value = totalDevices.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "在线",
                    value = onlineDevices.toString(),
                    color = SuccessGreen
                )
                StatItem(
                    label = "离线",
                    value = offlineDevices.toString(),
                    color = DeviceOffline
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
private fun DeviceTypeFilterSection(
    selectedType: DeviceType?,
    onTypeSelected: (DeviceType?) -> Unit
) {
    Column {
        Text(
            text = "设备类型",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { onTypeSelected(null) },
                    label = { Text("全部") }
                )
            }
            items(DeviceType.values()) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(getDeviceTypeName(type)) }
                )
            }
        }
    }
}

@Composable
private fun DeviceListSection(
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
                text = "添加设备到您的智能家居",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// 辅助函数
private fun getDeviceTypeName(deviceType: DeviceType): String {
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
        else -> "其他"
    }
}
