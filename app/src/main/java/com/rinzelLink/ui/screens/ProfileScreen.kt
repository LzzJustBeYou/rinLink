package com.rinzelLink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.rinzelLink.ui.theme.*

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 用户信息头部
        item {
            UserProfileHeader(
                user = uiState.user,
                onEditProfile = viewModel::editProfile
            )
        }
        
        // 统计信息
        item {
            ProfileStatsSection(
                totalDevices = uiState.totalDevices,
                totalRooms = uiState.totalRooms,
                totalScenes = uiState.totalScenes
            )
        }
        
        // 设置选项
        item {
            SettingsSection(
                settings = uiState.settings,
                onSettingClick = viewModel::navigateToSetting
            )
        }
        
        // 关于信息
        item {
            AboutSection(
                onAboutClick = viewModel::navigateToAbout
            )
        }
    }
}

@Composable
private fun UserProfileHeader(
    user: UserProfile?,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "头像",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用户信息
            Text(
                text = user?.name ?: "智能家居用户",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user?.email ?: "user@example.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 编辑按钮
            OutlinedButton(
                onClick = onEditProfile,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = null
                ).copy(
                    width = 1.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("编辑资料")
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(
    totalDevices: Int,
    totalRooms: Int,
    totalScenes: Int
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
                text = "我的数据",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "设备",
                    value = totalDevices.toString(),
                    color = AqaraGreen
                )
                StatItem(
                    label = "房间",
                    value = totalRooms.toString(),
                    color = AqaraBlue
                )
                StatItem(
                    label = "场景",
                    value = totalScenes.toString(),
                    color = AqaraPurple
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SettingsSection(
    settings: List<SettingItem>,
    onSettingClick: (String) -> Unit
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
                .padding(16.dp)
        ) {
            Text(
                text = "设置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            settings.forEach { setting ->
                SettingItemRow(
                    setting = setting,
                    onClick = { onSettingClick(setting.id) }
                )
                if (setting != settings.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    setting: SettingItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = setting.icon,
            contentDescription = setting.title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = setting.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (setting.subtitle != null) {
                Text(
                    text = setting.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "进入",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AboutSection(
    onAboutClick: () -> Unit
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
                .padding(16.dp)
        ) {
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "关于应用",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "RinLink 智能家居",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "版本 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "进入",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// 数据类
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null
)

data class SettingItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector
)
```

创建ProfileViewModel：

```kotlin:/Users/rinzel/rinzel_code/android/RinLink/app/src/main/java/com/rinzelLink/ui/viewmodels/ProfileViewModel.kt
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
