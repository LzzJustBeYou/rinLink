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
import com.rinzelLink.core.scene.Scene
import com.rinzelLink.ui.theme.*

@Composable
fun SceneScreen(
    modifier: Modifier = Modifier,
    viewModel: SceneViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部标题
        item {
            SceneHeader()
        }
        
        // 推荐场景
        item {
            RecommendedScenesSection(
                scenes = uiState.recommendedScenes,
                onSceneClick = viewModel::executeScene
            )
        }
        
        // 我的场景
        item {
            MyScenesSection(
                scenes = uiState.myScenes,
                onSceneClick = viewModel::executeScene,
                onEditScene = viewModel::editScene,
                onDeleteScene = viewModel::deleteScene
            )
        }
        
        // 场景模板
        item {
            SceneTemplatesSection(
                templates = uiState.sceneTemplates,
                onTemplateClick = viewModel::createSceneFromTemplate
            )
        }
    }
}

@Composable
private fun SceneHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "智能场景",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "自动化您的智能家居",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        
        IconButton(
            onClick = { /* 添加新场景 */ }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加场景",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecommendedScenesSection(
    scenes: List<Scene>,
    onSceneClick: (String) -> Unit
) {
    Column {
        Text(
            text = "推荐场景",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scenes) { scene ->
                RecommendedSceneCard(
                    scene = scene,
                    onClick = { onSceneClick(scene.id) }
                )
            }
        }
    }
}

@Composable
private fun RecommendedSceneCard(
    scene: Scene,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = scene.color?.let { Color(it) } 
                ?: MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getSceneIcon(scene.icon),
                contentDescription = scene.name,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scene.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = 18.sp
            )
            Text(
                text = scene.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun MyScenesSection(
    scenes: List<Scene>,
    onSceneClick: (String) -> Unit,
    onEditScene: (String) -> Unit,
    onDeleteScene: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的场景",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { /* 管理场景 */ }) {
                Text("管理")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        if (scenes.isEmpty()) {
            EmptyScenesCard()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(scenes) { scene ->
                    MySceneCard(
                        scene = scene,
                        onClick = { onSceneClick(scene.id) },
                        onEdit = { onEditScene(scene.id) },
                        onDelete = { onDeleteScene(scene.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MySceneCard(
    scene: Scene,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 场景图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        scene.color?.let { Color(it) } 
                            ?: MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getSceneIcon(scene.icon),
                    contentDescription = scene.name,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 场景信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = scene.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = scene.description ?: "智能场景",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "执行次数: ${scene.executionCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            // 操作按钮
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyScenesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "无场景",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无自定义场景",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "创建您的第一个智能场景",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SceneTemplatesSection(
    templates: List<SceneTemplate>,
    onTemplateClick: (String) -> Unit
) {
    Column {
        Text(
            text = "场景模板",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(templates) { template ->
                SceneTemplateCard(
                    template = template,
                    onClick = { onTemplateClick(template.id) }
                )
            }
        }
    }
}

@Composable
private fun SceneTemplateCard(
    template: SceneTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getSceneIcon(template.icon),
                contentDescription = template.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = template.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

// 辅助函数
@Composable
private fun getSceneIcon(iconName: String?): ImageVector {
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

// 数据类
data class SceneTemplate(
    val id: String,
    val name: String,
    val icon: String?,
    val description: String?
)
