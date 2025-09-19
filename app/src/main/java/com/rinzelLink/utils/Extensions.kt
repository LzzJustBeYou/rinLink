package com.rinzelLink.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.DeviceType
import com.rinzelLink.core.room.Room
import com.rinzelLink.core.room.RoomType
import com.rinzelLink.core.scene.Scene
import java.text.SimpleDateFormat
import java.util.*

/**
 * 设备扩展函数
 */
fun Device.getDisplayName(): String {
    return name.ifEmpty { getDeviceTypeDisplayName(type) }
}

fun Device.getStatusText(): String {
    return if (isOnline) "在线" else "离线"
}

fun Device.getStatusColor(): Color {
    return if (isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
}

fun Device.getPropertyValue(propertyName: String): Any? {
    return properties[propertyName]?.value
}

fun Device.hasCapability(capability: com.rinzelLink.core.device.Capability): Boolean {
    return capabilities.contains(capability)
}

/**
 * 房间扩展函数
 */
fun Room.getDisplayName(): String {
    return name.ifEmpty { getRoomTypeDisplayName(type) }
}

fun Room.getDeviceCountText(): String {
    return "$deviceCount 个设备"
}

fun Room.getStatusText(): String {
    return if (isActive) "活跃" else "非活跃"
}

fun Room.getStatusColor(): Color {
    return if (isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
}

/**
 * 场景扩展函数
 */
fun Scene.getDisplayName(): String {
    return name.ifEmpty { "未命名场景" }
}

fun Scene.getExecutionCountText(): String {
    return "执行 $executionCount 次"
}

fun Scene.getLastExecutedText(): String {
    return lastExecutedTime?.let { 
        formatTimestamp(it) 
    } ?: "从未执行"
}

fun Scene.isRecentlyExecuted(): Boolean {
    return lastExecutedTime?.let { 
        System.currentTimeMillis() - it < 300000 // 5分钟内
    } ?: false
}

/**
 * 时间格式化函数
 */
fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "刚刚"
        diff < 3600000 -> "${diff / 60000}分钟前"
        diff < 86400000 -> "${diff / 3600000}小时前"
        diff < 604800000 -> "${diff / 86400000}天前"
        else -> {
            val formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

fun formatDateTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

/**
 * 颜色扩展函数
 */
fun Color.toHex(): String {
    return String.format("#%08X", toArgb())
}

fun String.toColor(): Color? {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * 设备类型显示名称
 */
fun getDeviceTypeDisplayName(deviceType: DeviceType): String {
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
        else -> "其他设备"
    }
}

/**
 * 房间类型显示名称
 */
fun getRoomTypeDisplayName(roomType: RoomType): String {
    return when (roomType) {
        RoomType.LIVING_ROOM -> "客厅"
        RoomType.BEDROOM -> "卧室"
        RoomType.KITCHEN -> "厨房"
        RoomType.BATHROOM -> "卫生间"
        RoomType.STUDY -> "书房"
        RoomType.DINING_ROOM -> "餐厅"
        RoomType.BALCONY -> "阳台"
        RoomType.GARAGE -> "车库"
        RoomType.GARDEN -> "花园"
        RoomType.OFFICE -> "办公室"
        RoomType.CORRIDOR -> "走廊"
        RoomType.STORAGE -> "储藏室"
        else -> "其他房间"
    }
}

/**
 * 协议显示名称
 */
fun getProtocolDisplayName(protocol: com.rinzelLink.core.device.Protocol): String {
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

/**
 * 能力显示名称
 */
fun getCapabilityDisplayName(capability: com.rinzelLink.core.device.Capability): String {
    return when (capability) {
        com.rinzelLink.core.device.Capability.ON_OFF -> "开关控制"
        com.rinzelLink.core.device.Capability.DIMMING -> "调光"
        com.rinzelLink.core.device.Capability.COLOR -> "颜色控制"
        com.rinzelLink.core.device.Capability.TEMPERATURE -> "温度控制"
        com.rinzelLink.core.device.Capability.HUMIDITY -> "湿度控制"
        com.rinzelLink.core.device.Capability.MOTION -> "运动检测"
        com.rinzelLink.core.device.Capability.LIGHT_SENSOR -> "光线传感器"
        com.rinzelLink.core.device.Capability.SOUND -> "声音控制"
        com.rinzelLink.core.device.Capability.SCHEDULE -> "定时功能"
        com.rinzelLink.core.device.Capability.SCENE -> "场景支持"
        com.rinzelLink.core.device.Capability.GROUP -> "分组控制"
        com.rinzelLink.core.device.Capability.REMOTE -> "远程控制"
        com.rinzelLink.core.device.Capability.VOICE -> "语音控制"
        com.rinzelLink.core.device.Capability.ENERGY_MONITOR -> "能耗监控"
    }
}

/**
 * 属性值格式化
 */
fun formatPropertyValue(value: Any?): String {
    return when (value) {
        is Boolean -> if (value) "开启" else "关闭"
        is Number -> {
            when (value) {
                is Double -> "%.1f".format(value)
                is Float -> "%.1f".format(value)
                else -> value.toString()
            }
        }
        is String -> value
        null -> "未知"
        else -> value.toString()
    }
}

/**
 * 属性名称格式化
 */
fun formatPropertyName(propertyName: String): String {
    return when (propertyName) {
        "power" -> "开关"
        "brightness" -> "亮度"
        "color" -> "颜色"
        "color_temp" -> "色温"
        "temperature" -> "温度"
        "humidity" -> "湿度"
        "pressure" -> "气压"
        "motion" -> "运动检测"
        "light_level" -> "光线强度"
        "sound_level" -> "声音强度"
        "position" -> "位置"
        "mode" -> "模式"
        "target_temp" -> "目标温度"
        "recording" -> "录制"
        "battery" -> "电量"
        "signal" -> "信号强度"
        else -> propertyName
    }
}

/**
 * 单位格式化
 */
fun formatPropertyValueWithUnit(propertyName: String, value: Any?): String {
    val formattedValue = formatPropertyValue(value)
    val unit = when (propertyName) {
        "brightness" -> "%"
        "temperature" -> "°C"
        "humidity" -> "%"
        "pressure" -> "hPa"
        "light_level" -> "lux"
        "sound_level" -> "dB"
        "position" -> "%"
        "battery" -> "%"
        "signal" -> "dBm"
        else -> ""
    }
    return if (unit.isNotEmpty()) "$formattedValue$unit" else formattedValue
}

/**
 * 状态指示器颜色
 */
@Composable
fun getStatusColor(isOnline: Boolean): androidx.compose.ui.graphics.Color {
    return if (isOnline) androidx.compose.ui.graphics.Color(0xFF4CAF50) 
    else androidx.compose.ui.graphics.Color(0xFF9E9E9E)
}

/**
 * 优先级颜色
 */
@Composable
fun getPriorityColor(priority: String): androidx.compose.ui.graphics.Color {
    return when (priority.lowercase()) {
        "high", "高" -> androidx.compose.ui.graphics.Color(0xFFF44336)
        "medium", "中" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        "low", "低" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        else -> androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    }
}

/**
 * 温度颜色
 */
@Composable
fun getTemperatureColor(temperature: Double): androidx.compose.ui.graphics.Color {
    return when {
        temperature < 0 -> androidx.compose.ui.graphics.Color(0xFF2196F3) // 蓝色 - 冷
        temperature < 15 -> androidx.compose.ui.graphics.Color(0xFF00BCD4) // 青色 - 凉
        temperature < 25 -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // 绿色 - 舒适
        temperature < 30 -> androidx.compose.ui.graphics.Color(0xFFFF9800) // 橙色 - 温暖
        else -> androidx.compose.ui.graphics.Color(0xFFF44336) // 红色 - 热
    }
}

/**
 * 湿度颜色
 */
@Composable
fun getHumidityColor(humidity: Double): androidx.compose.ui.graphics.Color {
    return when {
        humidity < 30 -> androidx.compose.ui.graphics.Color(0xFFF44336) // 红色 - 干燥
        humidity < 50 -> androidx.compose.ui.graphics.Color(0xFFFF9800) // 橙色 - 较干
        humidity < 70 -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // 绿色 - 舒适
        humidity < 80 -> androidx.compose.ui.graphics.Color(0xFF00BCD4) // 青色 - 较湿
        else -> androidx.compose.ui.graphics.Color(0xFF2196F3) // 蓝色 - 潮湿
    }
}
