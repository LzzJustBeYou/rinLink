package com.rinzelLink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.rinzelLink.ui.components.DeviceControlPanel
import com.rinzelLink.ui.theme.*

@Composable
fun DeviceDetailScreen(
    deviceId: String,
    modifier: Modifier = Modifier,
    viewModel: DeviceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 设备头部信息
        item {
            DeviceHeaderSection(
                device = uiState.device,
                onToggleOnline = viewModel::toggleDeviceOnline
            )
        }
        
        // 设备控制面板
        item {
            if (uiState.device != null) {
                DeviceControlPanel(
                    device = uiState.device,
                    onPropertyChange = viewModel::updateDeviceProperty
                )
            }
        }
        
        // 设备信息
        item {
            DeviceInfoSection(device = uiState.device)
        }
        
        // 操作历史
        item {
            DeviceHistorySection(
                history = uiState.deviceHistory,
                onClearHistory = viewModel::clearHistory
            )
        }
    }
}

@Composable
private fun DeviceHeaderSection(
    device: Device?,
    onToggleOnline: () -> Unit
) {
    if (device == null) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isOnline) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
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
                        imageVector = getDeviceIcon(device.type),
                        contentDescription = device.name,
                        tint = if (device.isOnline) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = device.model ?: "未知型号",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // 在线状态切换
                Switch(
                    checked = device.isOnline,
                    onCheckedChange = { onToggleOnline() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 设备状态信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusItem(
                    label = "状态",
                    value = if (device.isOnline) "在线" else "离线",
                    color = if (device.isOnline) SuccessGreen else DeviceOffline
                )
                StatusItem(
                    label = "协议",
                    value = getProtocolName(device.protocol),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatusItem(
                    label = "房间",
                    value = device.room ?: "未分配",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
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
private fun DeviceInfoSection(device: Device?) {
    if (device == null) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "设备信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InfoRow("设备ID", device.did)
            InfoRow("设备类型", getDeviceTypeName(device.type))
            InfoRow("通信协议", getProtocolName(device.protocol))
            InfoRow("制造商", device.manufacturer ?: "未知")
            InfoRow("所属房间", device.room ?: "未分配")
            InfoRow("所属区域", device.zone ?: "未分配")
            InfoRow("最后在线", formatLastSeen(device.lastSeen))
            InfoRow("标签", device.tags.joinToString(", ").ifEmpty { "无" })
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DeviceHistorySection(
    history: List<DeviceHistoryItem>,
    onClearHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                Text(
                    text = "操作历史",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onClearHistory) {
                    Text("清空")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (history.isEmpty()) {
                Text(
                    text = "暂无操作历史",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                history.forEach { item ->
                    HistoryItem(item = item)
                    if (item != history.last()) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(item: DeviceHistoryItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getHistoryIcon(item.type),
            contentDescription = item.type,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatTimestamp(item.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
private fun getHistoryIcon(type: String): ImageVector {
    return when (type) {
        "power" -> Icons.Default.Power
        "brightness" -> Icons.Default.Lightbulb
        "temperature" -> Icons.Default.Thermostat
        "motion" -> Icons.Default.DirectionsRun
        "online" -> Icons.Default.Wifi
        "offline" -> Icons.Default.WifiOff
        else -> Icons.Default.Info
    }
}

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

private fun getProtocolName(protocol: com.rinzelLink.core.device.Protocol): String {
    return when (protocol) {
        com.rinzelLink.core.device.Protocol.LAN -> "局域网"
        com.rinzelLink.core.device.Protocol.ZIGBEE -> "Zigbee"
        com.rinzelLink.core.device.Protocol.WEBSOCKET -> "WebSocket"
        com.rinzelLink.core.device.Protocol.BLE -> "蓝牙"
        com.rinzelLink.core.device.Protocol.MQTT -> "MQTT"
        com.rinzelLink.core.device.Protocol.WIFI -> "WiFi"
        com.rinzelLink.core.device.Protocol.THREAD -> "Thread"
        com.rinzelLink.core.device.Protocol.MATTER -> "Matter"
    }
}

private fun formatLastSeen(timestamp: Long): String {
    if (timestamp == 0L) return "从未在线"
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "刚刚"
        diff < 3600000 -> "${diff / 60000}分钟前"
        diff < 86400000 -> "${diff / 3600000}小时前"
        else -> "${diff / 86400000}天前"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "刚刚"
        diff < 3600000 -> "${diff / 60000}分钟前"
        diff < 86400000 -> "${diff / 3600000}小时前"
        else -> "${diff / 86400000}天前"
    }
}

// 数据类
data class DeviceHistoryItem(
    val type: String,
    val description: String,
    val timestamp: Long
)
