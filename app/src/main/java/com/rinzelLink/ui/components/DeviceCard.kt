package com.rinzelLink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.ui.theme.*

@Composable
fun DeviceCard(
    device: Device,
    modifier: Modifier = Modifier,
    onToggle: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = if (device.isOnline) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 设备头部信息
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
                
                // 在线状态指示器
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 设备属性
            if (device.properties.isNotEmpty()) {
                DeviceProperties(
                    properties = device.properties,
                    deviceType = device.type
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 操作按钮
            if (onToggle != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (device.isOnline) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (device.isOnline) "关闭" else "开启",
                            color = if (device.isOnline) 
                                MaterialTheme.colorScheme.onPrimary 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceProperties(
    properties: Map<String, com.rinzelLink.core.device.Property>,
    deviceType: DeviceType
) {
    when (deviceType) {
        DeviceType.LIGHT -> {
            LightProperties(properties)
        }
        DeviceType.SENSOR -> {
            SensorProperties(properties)
        }
        DeviceType.THERMOSTAT -> {
            ThermostatProperties(properties)
        }
        else -> {
            GenericProperties(properties)
        }
    }
}

@Composable
private fun LightProperties(properties: Map<String, com.rinzelLink.core.device.Property>) {
    val power = properties["power"]?.value as? Boolean ?: false
    val brightness = properties["brightness"]?.value as? Number?.toInt() ?: 0
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PropertyItem(
            label = "开关",
            value = if (power) "开启" else "关闭",
            icon = if (power) Icons.Default.Lightbulb else Icons.Default.LightbulbOutline
        )
        PropertyItem(
            label = "亮度",
            value = "$brightness%",
            icon = Icons.Default.Brightness6
        )
    }
}

@Composable
private fun SensorProperties(properties: Map<String, com.rinzelLink.core.device.Property>) {
    val temperature = properties["temperature"]?.value as? Number?.toDouble() ?: 0.0
    val humidity = properties["humidity"]?.value as? Number?.toDouble() ?: 0.0
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PropertyItem(
            label = "温度",
            value = "${temperature}°C",
            icon = Icons.Default.Thermostat
        )
        PropertyItem(
            label = "湿度",
            value = "${humidity}%",
            icon = Icons.Default.WaterDrop
        )
    }
}

@Composable
private fun ThermostatProperties(properties: Map<String, com.rinzelLink.core.device.Property>) {
    val temperature = properties["temperature"]?.value as? Number?.toDouble() ?: 0.0
    val mode = properties["mode"]?.value as? String ?: "自动"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PropertyItem(
            label = "温度",
            value = "${temperature}°C",
            icon = Icons.Default.Thermostat
        )
        PropertyItem(
            label = "模式",
            value = mode,
            icon = Icons.Default.Settings
        )
    }
}

@Composable
private fun GenericProperties(properties: Map<String, com.rinzelLink.core.device.Property>) {
    // 显示前两个属性
    val entries = properties.entries.take(2)
    if (entries.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            entries.forEachIndexed { index, entry ->
                PropertyItem(
                    label = entry.key,
                    value = entry.value.value.toString(),
                    icon = Icons.Default.Info
                )
                if (index < entries.size - 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PropertyItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

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
