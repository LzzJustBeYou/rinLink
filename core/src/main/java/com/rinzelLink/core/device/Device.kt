package com.rinzelLink.core.device

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 设备抽象类
 * 支持多协议状态管理，房间/区域分类
 */
@Parcelize
data class Device(
    val did: String,                    // 设备ID
    val name: String,                   // 设备名称
    val type: DeviceType,              // 设备类型
    val protocol: Protocol,            // 通信协议
    val room: String? = null,          // 所属房间
    val zone: String? = null,          // 所属区域
    val model: String? = null,         // 设备型号
    val manufacturer: String? = null,  // 制造商
    val isOnline: Boolean = false,     // 在线状态
    val lastSeen: Long = 0L,          // 最后在线时间
    val properties: Map<String, Property> = emptyMap(), // 设备属性
    val tags: List<String> = emptyList(), // 标签
    val capabilities: List<Capability> = emptyList() // 设备能力
) : Parcelable

/**
 * 设备类型枚举
 */
enum class DeviceType {
    LIGHT,           // 灯具
    SWITCH,          // 开关
    SENSOR,          // 传感器
    THERMOSTAT,      // 温控器
    CAMERA,          // 摄像头
    DOOR_LOCK,       // 门锁
    CURTAIN,         // 窗帘
    AIR_CONDITIONER, // 空调
    FAN,             // 风扇
    SPEAKER,         // 音响
    TV,              // 电视
    REFRIGERATOR,    // 冰箱
    WASHING_MACHINE, // 洗衣机
    ROBOT_VACUUM,    // 扫地机器人
    OTHER            // 其他
}

/**
 * 通信协议枚举
 */
enum class Protocol {
    LAN,        // 局域网
    ZIGBEE,     // Zigbee
    WEBSOCKET,  // WebSocket云端
    BLE,        // 蓝牙
    MQTT,       // MQTT
    WIFI,       // WiFi直连
    THREAD,     // Thread协议
    MATTER      // Matter协议
}

/**
 * 设备能力枚举
 */
enum class Capability {
    ON_OFF,         // 开关控制
    DIMMING,        // 调光
    COLOR,          // 颜色控制
    TEMPERATURE,    // 温度控制
    HUMIDITY,       // 湿度控制
    MOTION,         // 运动检测
    LIGHT_SENSOR,   // 光线传感器
    SOUND,          // 声音控制
    SCHEDULE,       // 定时功能
    SCENE,          // 场景支持
    GROUP,          // 分组控制
    REMOTE,         // 远程控制
    VOICE,          // 语音控制
    ENERGY_MONITOR  // 能耗监控
}
