package com.rinzelLink.core.scene

import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Property
import com.rinzelLink.core.module.Command
import com.rinzelLink.core.module.CommandPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 场景服务
 * 负责场景管理、定时任务、条件触发、多设备联动
 */
@Singleton
class SceneService @Inject constructor() {
    
    private val scenes = mutableMapOf<String, Scene>()
    private val _scenesFlow = MutableSharedFlow<List<Scene>>()
    val scenesFlow: SharedFlow<List<Scene>> = _scenesFlow.asSharedFlow()
    
    private val _sceneExecutionFlow = MutableSharedFlow<SceneExecutionEvent>()
    val sceneExecutionFlow: SharedFlow<SceneExecutionEvent> = _sceneExecutionFlow.asSharedFlow()
    
    /**
     * 获取所有场景
     */
    fun getAllScenes(): List<Scene> {
        return scenes.values.toList()
    }
    
    /**
     * 根据ID获取场景
     */
    fun getSceneById(sceneId: String): Scene? {
        return scenes[sceneId]
    }
    
    /**
     * 添加场景
     */
    fun addScene(scene: Scene) {
        scenes[scene.id] = scene
        _scenesFlow.tryEmit(scenes.values.toList())
    }
    
    /**
     * 更新场景
     */
    fun updateScene(scene: Scene) {
        scenes[scene.id] = scene
        _scenesFlow.tryEmit(scenes.values.toList())
    }
    
    /**
     * 删除场景
     */
    fun deleteScene(sceneId: String) {
        scenes.remove(sceneId)
        _scenesFlow.tryEmit(scenes.values.toList())
    }
    
    /**
     * 执行场景
     */
    suspend fun executeScene(sceneId: String, devices: List<Device>): SceneExecutionResult {
        val scene = scenes[sceneId] ?: return SceneExecutionResult(
            success = false,
            sceneId = sceneId,
            errorMessage = "场景不存在"
        )
        
        try {
            _sceneExecutionFlow.tryEmit(SceneExecutionEvent.Started(sceneId))
            
            val commands = mutableListOf<Command>()
            
            // 构建命令列表
            scene.actions.forEach { action ->
                val device = devices.find { it.did == action.deviceId }
                if (device != null) {
                    val property = device.properties[action.propertyName]
                    if (property != null) {
                        val command = Command(
                            device = device,
                            property = property,
                            value = action.value,
                            priority = CommandPriority.NORMAL
                        )
                        commands.add(command)
                    }
                }
            }
            
            // 执行命令
            val results = executeCommands(commands)
            
            val success = results.all { it.success }
            
            _sceneExecutionFlow.tryEmit(
                if (success) SceneExecutionEvent.Completed(sceneId)
                else SceneExecutionEvent.Failed(sceneId, "部分命令执行失败")
            )
            
            return SceneExecutionResult(
                success = success,
                sceneId = sceneId,
                executedCommands = results.size,
                failedCommands = results.count { !it.success }
            )
            
        } catch (e: Exception) {
            _sceneExecutionFlow.tryEmit(SceneExecutionEvent.Failed(sceneId, e.message ?: "未知错误"))
            return SceneExecutionResult(
                success = false,
                sceneId = sceneId,
                errorMessage = e.message
            )
        }
    }
    
    /**
     * 检查场景触发条件
     */
    fun checkTriggerConditions(sceneId: String, devices: List<Device>): Boolean {
        val scene = scenes[sceneId] ?: return false
        
        return scene.triggers.all { trigger ->
            when (trigger.type) {
                TriggerType.DEVICE_STATE -> {
                    val device = devices.find { it.did == trigger.deviceId }
                    device?.properties?.get(trigger.propertyName)?.value == trigger.expectedValue
                }
                TriggerType.TIME -> {
                    // 时间触发逻辑
                    true // 简化实现
                }
                TriggerType.MOTION -> {
                    val device = devices.find { it.did == trigger.deviceId }
                    device?.properties?.get("motion")?.value == true
                }
                TriggerType.TEMPERATURE -> {
                    val device = devices.find { it.did == trigger.deviceId }
                    val temperature = device?.properties?.get("temperature")?.value as? Number
                    when (trigger.operator) {
                        ">" -> temperature?.toDouble() ?: 0.0 > (trigger.expectedValue as? Number)?.toDouble() ?: 0.0
                        "<" -> temperature?.toDouble() ?: 0.0 < (trigger.expectedValue as? Number)?.toDouble() ?: 0.0
                        "=" -> temperature?.toDouble() ?: 0.0 == (trigger.expectedValue as? Number)?.toDouble() ?: 0.0
                        else -> false
                    }
                }
            }
        }
    }
    
    /**
     * 执行命令（模拟）
     */
    private suspend fun executeCommands(commands: List<Command>): List<com.rinzelLink.core.module.CommandResult> {
        // 这里应该调用ModuleManager执行命令
        // 暂时返回模拟结果
        return commands.map { command ->
            com.rinzelLink.core.module.CommandResult(
                success = true,
                deviceId = command.device.did,
                propertyName = command.property.name
            )
        }
    }
}

/**
 * 场景定义
 */
data class Scene(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: Int? = null,
    val triggers: List<SceneTrigger> = emptyList(),
    val actions: List<SceneAction> = emptyList(),
    val isActive: Boolean = true,
    val createdTime: Long = System.currentTimeMillis(),
    val lastExecutedTime: Long? = null,
    val executionCount: Int = 0
)

/**
 * 场景触发条件
 */
data class SceneTrigger(
    val type: TriggerType,
    val deviceId: String? = null,
    val propertyName: String? = null,
    val expectedValue: Any? = null,
    val operator: String? = null,
    val timeExpression: String? = null
)

/**
 * 场景动作
 */
data class SceneAction(
    val deviceId: String,
    val propertyName: String,
    val value: Any,
    val delay: Long = 0L
)

/**
 * 触发类型
 */
enum class TriggerType {
    DEVICE_STATE,   // 设备状态
    TIME,           // 时间
    MOTION,         // 运动检测
    TEMPERATURE,    // 温度
    HUMIDITY,       // 湿度
    LIGHT_LEVEL,    // 光线强度
    SOUND_LEVEL     // 声音强度
}

/**
 * 场景执行事件
 */
sealed class SceneExecutionEvent {
    data class Started(val sceneId: String) : SceneExecutionEvent()
    data class Completed(val sceneId: String) : SceneExecutionEvent()
    data class Failed(val sceneId: String, val error: String) : SceneExecutionEvent()
}

/**
 * 场景执行结果
 */
data class SceneExecutionResult(
    val success: Boolean,
    val sceneId: String,
    val executedCommands: Int = 0,
    val failedCommands: Int = 0,
    val errorMessage: String? = null
)
