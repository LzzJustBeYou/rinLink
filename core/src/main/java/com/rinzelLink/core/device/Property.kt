package com.rinzelLink.core.device

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 设备属性抽象类
 * 支持主动上报与被动查询
 */
@Parcelize
data class Property(
    val siid: Int,                    // 服务ID
    val piid: Int,                    // 属性ID
    val name: String,                 // 属性名称
    val value: Any?,                  // 属性值
    val type: PropertyType,          // 属性类型
    val readable: Boolean = true,     // 是否可读
    val writable: Boolean = false,    // 是否可写
    val range: ValueRange? = null,    // 值范围
    val unit: String? = null,         // 单位
    val description: String? = null,  // 描述
    val lastUpdate: Long = 0L,       // 最后更新时间
    val metadata: Map<String, Any> = emptyMap() // 元数据
) : Parcelable

/**
 * 属性类型枚举
 */
enum class PropertyType {
    BOOLEAN,    // 布尔值
    INTEGER,    // 整数
    FLOAT,      // 浮点数
    STRING,     // 字符串
    ENUM,       // 枚举
    JSON,       // JSON对象
    ARRAY,      // 数组
    BINARY      // 二进制数据
}

/**
 * 值范围定义
 */
@Parcelize
data class ValueRange(
    val min: Number? = null,
    val max: Number? = null,
    val step: Number? = null,
    val enumValues: List<Any>? = null
) : Parcelable

/**
 * 常用属性常量
 */
object PropertyConstants {
    // 通用属性
    const val POWER = "power"                    // 电源开关
    const val BRIGHTNESS = "brightness"          // 亮度
    const val COLOR_TEMPERATURE = "color_temp"   // 色温
    const val COLOR_RGB = "rgb"                  // RGB颜色
    const val TEMPERATURE = "temperature"        // 温度
    const val HUMIDITY = "humidity"              // 湿度
    const val MOTION = "motion"                  // 运动检测
    const val LIGHT_LEVEL = "light_level"        // 光线强度
    const val SOUND_LEVEL = "sound_level"        // 声音强度
    const val BATTERY = "battery"                // 电池电量
    const val SIGNAL_STRENGTH = "signal"         // 信号强度
    
    // 空调相关
    const val TARGET_TEMP = "target_temp"        // 目标温度
    const val FAN_SPEED = "fan_speed"            // 风速
    const val MODE = "mode"                      // 模式
    const val SWING = "swing"                    // 摆风
    
    // 窗帘相关
    const val POSITION = "position"              // 位置
    const val ANGLE = "angle"                    // 角度
    
    // 门锁相关
    const val LOCK_STATUS = "lock_status"        // 锁状态
    const val DOOR_STATUS = "door_status"        // 门状态
    const val ALARM = "alarm"                    // 报警状态
}
