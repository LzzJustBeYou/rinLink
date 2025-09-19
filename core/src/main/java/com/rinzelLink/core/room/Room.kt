package com.rinzelLink.core.room

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * 房间/区域抽象类
 */
@Parcelize
data class Room(
    val id: String,
    val name: String,
    val type: RoomType,
    val zone: String? = null,           // 所属区域
    val icon: String? = null,          // 房间图标
    val color: Int? = null,            // 房间颜色
    val description: String? = null,   // 描述
    val deviceCount: Int = 0,          // 设备数量
    val isActive: Boolean = true,      // 是否激活
    val metadata: @RawValue Map<String, Any> = emptyMap()
) : Parcelable

/**
 * 房间类型枚举
 */
enum class RoomType {
    LIVING_ROOM,    // 客厅
    BEDROOM,        // 卧室
    KITCHEN,        // 厨房
    BATHROOM,       // 卫生间
    STUDY,          // 书房
    DINING_ROOM,    // 餐厅
    BALCONY,        // 阳台
    GARAGE,         // 车库
    GARDEN,         // 花园
    OFFICE,         // 办公室
    CORRIDOR,       // 走廊
    STORAGE,        // 储藏室
    OTHER           // 其他
}

/**
 * 设备分组抽象类
 */
@Parcelize
data class DeviceGroup(
    val id: String,
    val name: String,
    val type: GroupType,
    val deviceIds: List<String>,       // 设备ID列表
    val roomIds: List<String>? = null, // 房间ID列表（可选）
    val conditions: List<GroupCondition>? = null, // 分组条件
    val icon: String? = null,
    val color: Int? = null,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdTime: Long = System.currentTimeMillis(),
    val metadata: @RawValue Map<String, Any> = emptyMap()
) : Parcelable

/**
 * 分组类型枚举
 */
enum class GroupType {
    MANUAL,         // 手动分组
    BY_ROOM,        // 按房间分组
    BY_TYPE,        // 按设备类型分组
    BY_CAPABILITY,  // 按能力分组
    BY_TAG,         // 按标签分组
    SMART,          // 智能分组
    SCENE           // 场景分组
}

/**
 * 分组条件
 */
@Parcelize
data class GroupCondition(
    val field: String,        // 字段名
    val operator: Operator,   // 操作符
    val value: @RawValue Any, // 值
    val logic: Logic? = null // 逻辑连接符（AND/OR）
) : Parcelable

enum class Operator {
    EQUALS,         // 等于
    NOT_EQUALS,     // 不等于
    CONTAINS,       // 包含
    NOT_CONTAINS,   // 不包含
    GREATER_THAN,   // 大于
    LESS_THAN,      // 小于
    IN,             // 在列表中
    NOT_IN          // 不在列表中
}

enum class Logic {
    AND,            // 且
    OR              // 或
}
