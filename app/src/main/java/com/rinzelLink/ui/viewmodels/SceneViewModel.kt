package com.rinzelLink.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rinzelLink.core.scene.Scene
import com.rinzelLink.core.scene.SceneService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SceneViewModel @Inject constructor(
    private val sceneService: SceneService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SceneUiState())
    val uiState: StateFlow<SceneUiState> = _uiState.asStateFlow()
    
    init {
        loadScenes()
    }
    
    private fun loadScenes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                recommendedScenes = getMockRecommendedScenes(),
                myScenes = getMockMyScenes(),
                sceneTemplates = getMockSceneTemplates()
            )
        }
    }
    
    fun executeScene(sceneId: String) {
        viewModelScope.launch {
            // 执行场景
            val result = sceneService.executeScene(sceneId, emptyList())
            // 处理执行结果
        }
    }
    
    fun editScene(sceneId: String) {
        // 导航到编辑场景页面
    }
    
    fun deleteScene(sceneId: String) {
        viewModelScope.launch {
            sceneService.deleteScene(sceneId)
            loadScenes()
        }
    }
    
    fun createSceneFromTemplate(templateId: String) {
        // 根据模板创建新场景
    }
    
    private fun getMockRecommendedScenes(): List<Scene> {
        return listOf(
            Scene(
                id = "rec_001",
                name = "回家模式",
                description = "打开灯光，调节温度",
                icon = "home",
                color = 0xFF4CAF50
            ),
            Scene(
                id = "rec_002",
                name = "睡眠模式",
                description = "关闭灯光，调节温度",
                icon = "sleep",
                color = 0xFF9C27B0
            ),
            Scene(
                id = "rec_003",
                name = "观影模式",
                description = "调暗灯光，关闭窗帘",
                icon = "movie",
                color = 0xFF2196F3
            )
        )
    }
    
    private fun getMockMyScenes(): List<Scene> {
        return listOf(
            Scene(
                id = "my_001",
                name = "我的回家模式",
                description = "自定义回家场景",
                icon = "home",
                color = 0xFF4CAF50,
                executionCount = 15
            ),
            Scene(
                id = "my_002",
                name = "工作模式",
                description = "专注工作环境",
                icon = "work",
                color = 0xFFFF9800,
                executionCount = 8
            )
        )
    }
    
    private fun getMockSceneTemplates(): List<SceneTemplate> {
        return listOf(
            SceneTemplate(
                id = "template_001",
                name = "回家模式",
                icon = "home",
                description = "欢迎回家"
            ),
            SceneTemplate(
                id = "template_002",
                name = "睡眠模式",
                icon = "sleep",
                description = "准备睡觉"
            ),
            SceneTemplate(
                id = "template_003",
                name = "观影模式",
                icon = "movie",
                description = "家庭影院"
            ),
            SceneTemplate(
                id = "template_004",
                name = "聚会模式",
                icon = "party",
                description = "娱乐时光"
            ),
            SceneTemplate(
                id = "template_005",
                name = "节能模式",
                icon = "energy",
                description = "环保节能"
            )
        )
    }
}

data class SceneUiState(
    val recommendedScenes: List<Scene> = emptyList(),
    val myScenes: List<Scene> = emptyList(),
    val sceneTemplates: List<SceneTemplate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SceneTemplate(
    val id: String,
    val name: String,
    val icon: String?,
    val description: String?
)