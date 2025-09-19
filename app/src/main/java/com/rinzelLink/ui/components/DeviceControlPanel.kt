package com.rinzelLink.ui.components

import androidx.compose.foundation.layout.*
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
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.ui.theme.*

@Composable
fun DeviceControlPanel(
    device: Device,
    modifier: Modifier = Modifier,
    onPropertyChange: (String, Any) -> Unit = { _, _ -> }
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            // 设备头部
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
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = device.model ?: "未知型号",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // 在线状态
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (device.isOnline) 
                                    DeviceOnline 
                                else 
                                    DeviceOffline
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (device.isOnline) "在线" else "离线",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (device.isOnline) 
                            DeviceOnline 
                        else 
                            DeviceOffline
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 设备控制
            when (device.type) {
                DeviceType.LIGHT -> {
                    LightControlPanel(
                        device = device,
                        onPropertyChange = onPropertyChange
                    )
                }
                DeviceType.SENSOR -> {
                    SensorDisplayPanel(device = device)
                }
                DeviceType.THERMOSTAT -> {
                    ThermostatControlPanel(
                        device = device,
                        onPropertyChange = onPropertyChange
                    )
                }
                else -> {
                    GenericControlPanel(
                        device = device,
                        onPropertyChange = onPropertyChange
                    )
                }
            }
        }
    }
}

@Composable
private fun LightControlPanel(
    device: Device,
    onPropertyChange: (String, Any) -> Unit
) {
    val power = device.properties["power"]?.value as? Boolean ?: false
    val brightness = device.properties["brightness"]?.value as? Number?.toInt() ?: 0
    
    Column {
        // 开关控制
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "开关",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = power,
                onCheckedChange = { onPropertyChange("power", it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
        
        if (power) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 亮度控制
            Text(
                text = "亮度: $brightness%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = brightness.toFloat(),
                onValueChange = { onPropertyChange("brightness", it.toInt()) },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun SensorDisplayPanel(device: Device) {
    Column {
        device.properties.forEach { (key, property) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getPropertyDisplayName(key),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatPropertyValue(property.value),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ThermostatControlPanel(
    device: Device,
    onPropertyChange: (String, Any) -> Unit
) {
    val temperature = device.properties["temperature"]?.value as? Number?.toDouble() ?: 22.0
    val mode = device.properties["mode"]?.value as? String ?: "auto"
    
    Column {
        // 温度显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "当前温度",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${temperature}°C",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 模式选择
        Text(
            text = "模式",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("auto", "cool", "heat", "fan").forEach { modeOption ->
                FilterChip(
                    selected = mode == modeOption,
                    onClick = { onPropertyChange("mode", modeOption) },
                    label = { Text(getModeDisplayName(modeOption)) }
                )
            }
        }
    }
}

@Composable
private fun GenericControlPanel(
    device: Device,
    onPropertyChange: (String, Any) -> Unit
) {
    Column {
        device.properties.forEach { (key, property) ->
            when (property.value) {
                is Boolean -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getPropertyDisplayName(key),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = property.value as Boolean,
                            onCheckedChange = { onPropertyChange(key, it) }
                        )
                    }
                }
                is Number -> {
                    val numberValue = property.value as Number
                    Text(
                        text = "${getPropertyDisplayName(key)}: ${formatPropertyValue(numberValue)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                else -> {
                    Text(
                        text = "${getPropertyDisplayName(key)}: ${formatPropertyValue(property.value)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
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

private fun getPropertyDisplayName(key: String): String {
    return when (key) {
        "power" -> "开关"
        "brightness" -> "亮度"
        "temperature" -> "温度"
        "humidity" -> "湿度"
        "motion" -> "运动检测"
        "position" -> "位置"
        "mode" -> "模式"
        "recording" -> "录制"
        else -> key
    }
}

private fun formatPropertyValue(value: Any): String {
    return when (value) {
        is Boolean -> if (value) "开启" else "关闭"
        is Number -> {
            when (value) {
                is Double -> "%.1f".format(value)
                is Float -> "%.1f".format(value)
                else -> value.toString()
            }
        }
        else -> value.toString()
    }
}

private fun getModeDisplayName(mode: String): String {
    return when (mode) {
        "auto" -> "自动"
        "cool" -> "制冷"
        "heat" -> "制热"
        "fan" -> "送风"
        else -> mode
    }
}
