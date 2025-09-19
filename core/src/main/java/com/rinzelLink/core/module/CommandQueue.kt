package com.rinzelLink.core.module

import com.rinzelLink.core.device.Device
import com.rinzelLink.core.device.Property
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 命令队列管理器
 * 支持优先级、批量发送、重试、离线缓存
 */
@Singleton
class CommandQueue @Inject constructor() {
    
    private val queue = PriorityBlockingQueue<Command>(11) { c1, c2 ->
        c1.priority.ordinal - c2.priority.ordinal
    }
    
    private val offlineQueue = mutableListOf<Command>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val _queueStatus = MutableSharedFlow<QueueStatus>()
    val queueStatus: SharedFlow<QueueStatus> = _queueStatus.asSharedFlow()
    
    private var isProcessing = false
    
    /**
     * 入队命令
     */
    fun enqueue(command: Command) {
        queue.offer(command)
        _queueStatus.tryEmit(QueueStatus(queue.size, isProcessing))
        
        if (!isProcessing) {
            startProcessing()
        }
    }
    
    /**
     * 批量入队命令
     */
    fun enqueueBatch(commands: List<Command>) {
        commands.forEach { queue.offer(it) }
        _queueStatus.tryEmit(QueueStatus(queue.size, isProcessing))
        
        if (!isProcessing) {
            startProcessing()
        }
    }
    
    /**
     * 开始处理队列
     */
    private fun startProcessing() {
        if (isProcessing) return
        
        isProcessing = true
        scope.launch {
            while (queue.isNotEmpty()) {
                val command = queue.poll() ?: break
                processCommand(command)
            }
            isProcessing = false
            _queueStatus.tryEmit(QueueStatus(queue.size, isProcessing))
        }
    }
    
    /**
     * 处理单个命令
     */
    private suspend fun processCommand(command: Command) {
        // 这里应该调用实际的模块执行命令
        // 暂时模拟处理
        delay(100)
        
        // 如果处理失败且还有重试次数，重新入队
        if (command.retryCount > 0) {
            val retryCommand = command.copy(retryCount = command.retryCount - 1)
            queue.offer(retryCommand)
        }
    }
    
    /**
     * 清空队列
     */
    fun clear() {
        queue.clear()
        _queueStatus.tryEmit(QueueStatus(0, false))
    }
    
    /**
     * 获取队列状态
     */
    fun getQueueStatus(): QueueStatus {
        return QueueStatus(queue.size, isProcessing)
    }
    
    /**
     * 添加离线命令
     */
    fun addOfflineCommand(command: Command) {
        offlineQueue.add(command)
    }
    
    /**
     * 处理离线队列
     */
    fun processOfflineQueue() {
        if (offlineQueue.isNotEmpty()) {
            val commands = offlineQueue.toList()
            offlineQueue.clear()
            enqueueBatch(commands)
        }
    }
}

/**
 * 队列状态
 */
data class QueueStatus(
    val queueSize: Int,
    val isProcessing: Boolean
)
