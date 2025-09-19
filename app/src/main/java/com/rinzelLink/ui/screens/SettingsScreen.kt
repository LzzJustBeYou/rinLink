package com.rinzelLink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rinzelLink.ui.theme.MaterialTheme

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    message: String = "加载中..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoadingCard(
    modifier: Modifier = Modifier,
    message: String = "加载中..."
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
```

现在让我创建一个设备详情页面：

```kotlin:/Users/rinzel/rinzel_code/android/RinLink/app/src/main/java/com/rinzelLink/ui/screens/DeviceDetailScreen.kt
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
```

现在创建DeviceDetailViewModel：

```kotlin:/Users/rinzel/rinzel_code/android/RinLink/app/src/main/java/com/rinzelLink/ui/viewmodels/DeviceDetailViewModel.kt
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
class DeviceDetailViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()
    
    fun loadDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                device = getMockDevice(deviceId),
                deviceHistory = getMockDeviceHistory(deviceId)
            )
        }
    }
    
    fun toggleDeviceOnline() {
        viewModelScope.launch {
            val currentDevice = _uiState.value.device ?: return@launch
            val updatedDevice = currentDevice.copy(isOnline = !currentDevice.isOnline)
            _uiState.value = _uiState.value.copy(device = updatedDevice)
            
            // 添加历史记录
            addHistoryItem(
                if (updatedDevice.isOnline) "online" else "offline",
                if (updatedDevice.isOnline) "设备上线" else "设备离线"
            )
        }
    }
    
    fun updateDeviceProperty(propertyName: String, value: Any) {
        viewModelScope.launch {
            val currentDevice = _uiState.value.device ?: return@launch
            val updatedProperties = currentDevice.properties.toMutableMap()
            updatedProperties[propertyName] = Property(propertyName, value)
            
            val updatedDevice = currentDevice.copy(properties = updatedProperties)
            _uiState.value = _uiState.value.copy(device = updatedDevice)
            
            // 添加历史记录
            addHistoryItem(
                propertyName,
                "更新 $propertyName 为 $value"
            )
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deviceHistory = emptyList())
        }
    }
    
    private fun addHistoryItem(type: String, description: String) {
        val newItem = DeviceHistoryItem(
            type = type,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        val currentHistory = _uiState.value.deviceHistory.toMutableList()
        currentHistory.add(0, newItem) // 添加到开头
        if (currentHistory.size > 50) { // 限制历史记录数量
            currentHistory.removeAt(currentHistory.size - 1)
        }
        _uiState.value = _uiState.value.copy(deviceHistory = currentHistory)
    }
    
    private fun getMockDevice(deviceId: String): Device? {
        return when (deviceId) {
            "light_001" -> Device(
                did = "light_001",
                name = "客厅主灯",
                type = DeviceType.LIGHT,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                model = "Aqara LED Bulb",
                manufacturer = "绿米",
                isOnline = true,
                lastSeen = System.currentTimeMillis() - 300000, // 5分钟前
                properties = mapOf(
                    "power" to Property("power", false),
                    "brightness" to Property("brightness", 80),
                    "color_temp" to Property("color_temp", 4000)
                ),
                tags = listOf("客厅", "照明"),
                capabilities = listOf(
                    com.rinzelLink.core.device.Capability.ON_OFF,
                    com.rinzelLink.core.device.Capability.DIMMING,
                    com.rinzelLink.core.device.Capability.COLOR
                )
            )
            "sensor_001" -> Device(
                did = "sensor_001",
                name = "温度传感器",
                type = DeviceType.SENSOR,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                model = "Aqara Temperature Sensor",
                manufacturer = "绿米",
                isOnline = true,
                lastSeen = System.currentTimeMillis() - 60000, // 1分钟前
                properties = mapOf(
                    "temperature" to Property("temperature", 25.5),
                    "humidity" to Property("humidity", 60.0),
                    "pressure" to Property("pressure", 1013.25)
                ),
                tags = listOf("客厅", "传感器"),
                capabilities = listOf(
                    com.rinzelLink.core.device.Capability.TEMPERATURE,
                    com.rinzelLink.core.device.Capability.HUMIDITY
                )
            )
            "thermostat_001" -> Device(
                did = "thermostat_001",
                name = "客厅温控器",
                type = DeviceType.THERMOSTAT,
                protocol = Protocol.ZIGBEE,
                room = "living_room",
                model = "Aqara Thermostat",
                manufacturer = "绿米",
                isOnline = true,
                lastSeen = System.currentTimeMillis() - 120000, // 2分钟前
                properties = mapOf(
                    "temperature" to Property("temperature", 22.0),
                    "mode" to Property("mode", "auto"),
                    "target_temp" to Property("target_temp", 23.0)
                ),
                tags = listOf("客厅", "温控"),
                capabilities = listOf(
                    com.rinzelLink.core.device.Capability.TEMPERATURE,
                    com.rinzelLink.core.device.Capability.SCHEDULE
                )
            )
            else -> null
        }
    }
    
    private fun getMockDeviceHistory(deviceId: String): List<DeviceHistoryItem> {
        val now = System.currentTimeMillis()
        return listOf(
            DeviceHistoryItem("power", "开启设备", now - 300000),
            DeviceHistoryItem("brightness", "调整亮度到 80%", now - 600000),
            DeviceHistoryItem("online", "设备上线", now - 900000),
            DeviceHistoryItem("brightness", "调整亮度到 60%", now - 1200000),
            DeviceHistoryItem("power", "关闭设备", now - 1800000)
        )
    }
}

data class DeviceDetailUiState(
    val device: Device? = null,
    val deviceHistory: List<DeviceHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DeviceHistoryItem(
    val type: String,
    val description: String,
    val timestamp: Long
)
```

现在让我创建一个设置页面：

```kotlin:/Users/rinzel/rinzel_code/android/RinLink/app/src/main/java/com/rinzelLink/ui/screens/SettingsScreen.kt
package com.rinzelLink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.rinzelLink.ui.theme.*

@Composable
fun SettingsScreen(
    settingId: String,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (settingId) {
            "notifications" -> {
                item {
                    NotificationSettingsSection(
                        settings = uiState.notificationSettings,
                        onSettingChange = viewModel::updateNotificationSetting
                    )
                }
            }
            "privacy" -> {
                item {
                    PrivacySettingsSection(
                        settings = uiState.privacySettings,
                        onSettingChange = viewModel::updatePrivacySetting
                    )
                }
            }
            "theme" -> {
                item {
                    ThemeSettingsSection(
                        currentTheme = uiState.currentTheme,
                        onThemeChange = viewModel::updateTheme
                    )
                }
            }
            "language" -> {
                item {
                    LanguageSettingsSection(
                        currentLanguage = uiState.currentLanguage,
                        onLanguageChange = viewModel::updateLanguage
                    )
                }
            }
            "backup" -> {
                item {
                    BackupSettingsSection(
                        onBackup = viewModel::backupData,
                        onRestore = viewModel::restoreData
                    )
                }
            }
            "help" -> {
                item {
                    HelpSection(
                        onContactSupport = viewModel::contactSupport,
                        onViewFAQ = viewModel::viewFAQ
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsSection(
    settings: Map<String, Boolean>,
    onSettingChange: (String, Boolean) -> Unit
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
            Text(
                text = "通知设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            NotificationSettingItem(
                title = "设备状态通知",
                subtitle = "设备上线/离线通知",
                checked = settings["device_status"] ?: true,
                onCheckedChange = { onSettingChange("device_status", it) }
            )
            
            NotificationSettingItem(
                title = "场景执行通知",
                subtitle = "场景执行成功/失败通知",
                checked = settings["scene_execution"] ?: true,
                onCheckedChange = { onSettingChange("scene_execution", it) }
            )
            
            NotificationSettingItem(
                title = "安全警报",
                subtitle = "门锁、摄像头等安全设备警报",
                checked = settings["security_alerts"] ?: true,
                onCheckedChange = { onSettingChange("security_alerts", it) }
            )
            
            NotificationSettingItem(
                title = "系统更新",
                subtitle = "应用和固件更新通知",
                checked = settings["system_updates"] ?: false,
                onCheckedChange = { onSettingChange("system_updates", it) }
            )
        }
    }
}

@Composable
private fun NotificationSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun PrivacySettingsSection(
    settings: Map<String, Boolean>,
    onSettingChange: (String, Boolean) -> Unit
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
            Text(
                text = "隐私设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PrivacySettingItem(
                title = "数据收集",
                subtitle = "允许收集使用数据以改善服务",
                checked = settings["data_collection"] ?: false,
                onCheckedChange = { onSettingChange("data_collection", it) }
            )
            
            PrivacySettingItem(
                title = "位置服务",
                subtitle = "基于位置提供智能服务",
                checked = settings["location_services"] ?: false,
                onCheckedChange = { onSettingChange("location_services", it) }
            )
            
            PrivacySettingItem(
                title = "云端同步",
                subtitle = "将数据同步到云端",
                checked = settings["cloud_sync"] ?: true,
                onCheckedChange = { onSettingChange("cloud_sync", it) }
            )
        }
    }
}

@Composable
private fun PrivacySettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun ThemeSettingsSection(
    currentTheme: String,
    onThemeChange: (String) -> Unit
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
            Text(
                text = "主题设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val themes = listOf("light", "dark", "auto")
            themes.forEach { theme ->
                ThemeOption(
                    theme = theme,
                    isSelected = currentTheme == theme,
                    onClick = { onThemeChange(theme) }
                )
                if (theme != themes.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    theme: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = getThemeDisplayName(theme),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LanguageSettingsSection(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
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
            Text(
                text = "语言设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val languages = listOf("zh", "en", "ja", "ko")
            languages.forEach { language ->
                LanguageOption(
                    language = language,
                    isSelected = currentLanguage == language,
                    onClick = { onLanguageChange(language) }
                )
                if (language != languages.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = getLanguageDisplayName(language),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BackupSettingsSection(
    onBackup: () -> Unit,
    onRestore: () -> Unit
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
            Text(
                text = "数据备份",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onBackup,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = "备份",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("备份数据")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onRestore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "恢复",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("恢复数据")
            }
        }
    }
}

@Composable
private fun HelpSection(
    onContactSupport: () -> Unit,
    onViewFAQ: () -> Unit
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
            Text(
                text = "帮助与支持",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HelpItem(
                title = "常见问题",
                subtitle = "查看常见问题解答",
                icon = Icons.Default.Help,
                onClick = onViewFAQ
            )
            
            HelpItem(
                title = "联系支持",
                subtitle = "获取技术支持",
                icon = Icons.Default.Support,
                onClick = onContactSupport
            )
        }
    }
}

@Composable
private fun HelpItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// 辅助函数
private fun getThemeDisplayName(theme: String): String {
    return when (theme) {
        "light" -> "浅色模式"
        "dark" -> "深色模式"
        "auto" -> "跟随系统"
        else -> theme
    }
}

private fun getLanguageDisplayName(language: String): String {
    return when (language) {
        "zh" -> "简体中文"
        "en" -> "English"
        "ja" -> "日本語"
        "ko" -> "한국어"
        else -> language
    }
}
```

现在创建SettingsViewModel：

```kotlin:/Users/rinzel/rinzel_code/android/RinLink/app/src/main/java/com/rinzelLink/ui/viewmodels/SettingsViewModel.kt
package com.rinzelLink.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _uiS
