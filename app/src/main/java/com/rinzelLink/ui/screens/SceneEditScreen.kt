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
import com.rinzelLink.ui.theme.*

@Composable
fun SceneEditScreen(
    sceneId: String? = null,
    modifier: Modifier = Modifier,
    viewModel: SceneEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 场景基本信息
        item {
            SceneBasicInfoSection(
                name = uiState.sceneName,
                description = uiState.sceneDescription,
                icon = uiState.selectedIcon,
                color = uiState.selectedColor,
                onNameChange = viewModel::updateSceneName,
                onDescriptionChange = viewModel::updateSceneDescription,
                onIconSelect = viewModel::selectIcon,
                onColorSelect = viewModel::selectColor
            )
        }
        
        // 触发条件
        item {
            TriggerConditionsSection(
                triggers = uiState.triggers,
                onAddTrigger = viewModel::addTrigger,
                onRemoveTrigger = viewModel::removeTrigger
            )
        }
        
        // 场景动作
        item {
            SceneActionsSection(
                actions = uiState.actions,
                devices = uiState.availableDevices,
                onAddAction = viewModel::addAction,
                onRemoveAction = viewModel::removeAction,
                onUpdateAction = viewModel::updateAction
            )
        }
        
        // 保存按钮
        item {
            SaveSceneButton(
                onSave = viewModel::saveScene,
                onCancel = viewModel::cancelEdit
            )
        }
    }
}

@Composable
private fun SceneBasicInfoSection(
    name: String,
    description: String,
    icon: String?,
    color: Int?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIconSelect: (String) -> Unit,
    onColorSelect: (Int) -> Unit
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
                text = "基本信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 场景名称
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("场景名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 场景描述
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("场景描述") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 图标选择
            Text(
                text = "选择图标",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getAvailableIcons()) { iconOption ->
                    IconSelectionChip(
                        icon = iconOption,
                        isSelected = icon == iconOption,
                        onClick = { onIconSelect(iconOption) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 颜色选择
            Text(
                text = "选择颜色",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getAvailableColors()) { colorOption ->
                    ColorSelectionChip(
                        color = colorOption,
                        isSelected = color == colorOption,
                        onClick = { onColorSelect(colorOption) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IconSelectionChip(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Icon(
                imageVector = getSceneIcon(icon),
                contentDescription = icon,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
private fun ColorSelectionChip(
    color: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(color))
            )
        }
    )
}

@Composable
private fun TriggerConditionsSection(
    triggers: List<SceneTrigger>,
    onAddTrigger: () -> Unit,
    onRemoveTrigger: (Int) -> Unit
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
                    text = "触发条件",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onAddTrigger) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加触发条件",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (triggers.isEmpty()) {
                Text(
                    text = "暂无触发条件",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                triggers.forEachIndexed { index, trigger ->
                    TriggerItem(
                        trigger = trigger,
                        onRemove = { onRemoveTrigger(index) }
                    )
                    if (index < triggers.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TriggerItem(
    trigger: SceneTrigger,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getTriggerIcon(trigger.type),
                contentDescription = "触发条件",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getTriggerTypeName(trigger.type),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = getTriggerDescription(trigger),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SceneActionsSection(
    actions: List<SceneAction>,
    devices: List<Device>,
    onAddAction: () -> Unit,
    onRemoveAction: (Int) -> Unit,
    onUpdateAction: (Int, SceneAction) -> Unit
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
                    text = "场景动作",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onAddAction) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加动作",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (actions.isEmpty()) {
                Text(
                    text = "暂无场景动作",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                actions.forEachIndexed { index, action ->
                    ActionItem(
                        action = action,
                        devices = devices,
                        onRemove = { onRemoveAction(index) },
                        onUpdate = { onUpdateAction(index, it) }
                    )
                    if (index < actions.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionItem(
    action: SceneAction,
    devices: List<Device>,
    onRemove: () -> Unit,
    onUpdate: (SceneAction) -> Unit
) {
    val device = devices.find { it.did == action.deviceId }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getDeviceIcon(device?.type ?: DeviceType.OTHER),
                contentDescription = "设备",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device?.name ?: "未知设备",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${action.propertyName}: ${action.value}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SaveSceneButton(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) {
            Text("取消")
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f)
        ) {
            Text("保存场景")
        }
    }
}

// 辅助函数
private fun getAvailableIcons(): List<String> {
    return listOf("home", "bed", "movie", "party", "work", "sleep", "wake", "away", "security", "energy")
}

private fun getAvailableColors(): List<Int> {
    return listOf(
        0xFF4CAF50, 0xFF2196F3, 0xFF9C27B0, 0xFFFF9800, 0xFFF44336,
        0xFF00BCD4, 0xFF795548, 0xFF607D8B, 0xFF8BC34A, 0xFFE91E63
    )
}

@Composable
private fun getSceneIcon(iconName: String): ImageVector {
    return when (iconName) {
        "home" -> Icons.Default.Home
        "bed" -> Icons.Default.Bed
        "movie" -> Icons.Default.Movie
        "party" -> Icons.Default.Celebration
        "work" -> Icons.Default.Work
        "sleep" -> Icons.Default.Bedtime
        "wake" -> Icons.Default.WbSunny
        "away" -> Icons.Default.ExitToApp
        "security" -> Icons.Default.Security
        "energy" -> Icons.Default.Eco
        else -> Icons.Default.AutoAwesome
    }
}

@Composable
private fun getTriggerIcon(triggerType: com.rinzelLink.core.scene.TriggerType): ImageVector {
    return when (triggerType) {
        com.rinzelLink.core.scene.TriggerType.DEVICE_STATE -> Icons.Default.DeviceHub
        com.rinzelLink.core.scene.TriggerType.TIME -> Icons.Default.Schedule
        com.rinzelLink.core.scene.TriggerType.MOTION -> Icons.Default.DirectionsRun
        com.rinzelLink.core.scene.TriggerType.TEMPERATURE -> Icons.Default.Thermostat
        com.rinzelLink.core.scene.TriggerType.HUMIDITY -> Icons.Default.WaterDrop
        com.rinzelLink.core.scene.TriggerType.LIGHT_LEVEL -> Icons.Default.Lightbulb
        com.rinzelLink.core.scene.TriggerType.SOUND_LEVEL -> Icons.Default.VolumeUp
    }
}

private fun getTriggerTypeName(triggerType: com.rinzelLink.core.scene.TriggerType): String {
    return when (triggerType) {
        com.rinzelLink.core.scene.TriggerType.DEVICE_STATE -> "设备状态"
        com.rinzelLink.core.scene.TriggerType.TIME -> "时间"
        com.rinzelLink.core.scene.TriggerType.MOTION -> "运动检测"
        com.rinzelLink.core.scene.TriggerType.TEMPERATURE -> "温度"
        com.rinzelLink.core.scene.TriggerType.HUMIDITY -> "湿度"
        com.rinzelLink.core.scene.TriggerType.LIGHT_LEVEL -> "光线强度"
        com.rinzelLink.core.scene.TriggerType.SOUND_LEVEL -> "声音强度"
    }
}

private fun getTriggerDescription(trigger: SceneTrigger): String {
    return when (trigger.type) {
        com.rinzelLink.core.scene.TriggerType.DEVICE_STATE -> "设备状态变化"
        com.rinzelLink.core.scene.TriggerType.TIME -> "定时触发"
        com.rinzelLink.core.scene.TriggerType.MOTION -> "检测到运动"
        com.rinzelLink.core.scene.TriggerType.TEMPERATURE -> "温度条件"
        com.rinzelLink.core.scene.TriggerType.HUMIDITY -> "湿度条件"
        com.rinzelLink.core.scene.TriggerType.LIGHT_LEVEL -> "光线条件"
        com.rinzelLink.core.scene.TriggerType.SOUND_LEVEL -> "声音条件"
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

// 数据类
data class SceneTrigger(
    val type: com.rinzelLink.core.scene.TriggerType,
    val deviceId: String? = null,
    val propertyName: String? = null,
    val expectedValue: Any? = null,
    val operator: String? = null
)

data class SceneAction(
    val deviceId: String,
    val propertyName: String,
    val value: Any,
    val delay: Long = 0L
)
