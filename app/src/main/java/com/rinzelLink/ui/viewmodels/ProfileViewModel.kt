package com.rinzelLink.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfileData()
    }
    
    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                user = getMockUser(),
                totalDevices = 12,
                totalRooms = 6,
                totalScenes = 8,
                settings = getMockSettings()
            )
        }
    }
    
    fun editProfile() {
        // 导航到编辑资料页面
    }
    
    fun navigateToSetting(settingId: String) {
        // 导航到对应设置页面
    }
    
    fun navigateToAbout() {
        // 导航到关于页面
    }
    
    private fun getMockUser(): UserProfile {
        return UserProfile(
            id = "user_001",
            name = "智能家居用户",
            email = "user@rinzellink.com"
        )
    }
    
    private fun getMockSettings(): List<SettingItem> {
        return listOf(
            SettingItem(
                id = "notifications",
                title = "通知设置",
                subtitle = "管理推送通知",
                icon = Icons.Default.Notifications
            ),
            SettingItem(
                id = "privacy",
                title = "隐私设置",
                subtitle = "数据隐私和安全",
                icon = Icons.Default.Security
            ),
            SettingItem(
                id = "theme",
                title = "主题设置",
                subtitle = "深色/浅色模式",
                icon = Icons.Default.Palette
            ),
            SettingItem(
                id = "language",
                title = "语言设置",
                subtitle = "应用语言",
                icon = Icons.Default.Language
            ),
            SettingItem(
                id = "backup",
                title = "数据备份",
                subtitle = "备份和恢复数据",
                icon = Icons.Default.Backup
            ),
            SettingItem(
                id = "help",
                title = "帮助与支持",
                subtitle = "常见问题和联系支持",
                icon = Icons.Default.Help
            )
        )
    }
}

data class ProfileUiState(
    val user: UserProfile? = null,
    val totalDevices: Int = 0,
    val totalRooms: Int = 0,
    val totalScenes: Int = 0,
    val settings: List<SettingItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)