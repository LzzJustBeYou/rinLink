package com.rinzelLink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.rinzelLink.core.module.ModuleManager
import com.rinzelLink.core.room.RoomManager
import com.rinzelLink.ui.theme.RinLinkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主Activity
 * @AndroidEntryPoint 注解告诉Hilt这个Activity需要依赖注入
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // @Inject 注解告诉Hilt自动注入这些依赖
    @Inject lateinit var moduleManager: ModuleManager
    @Inject lateinit var roomManager: RoomManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            RinLinkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        
        // 现在可以直接使用注入的依赖
        lifecycleScope.launch {
            initializeServices()
        }
    }
    
    private suspend fun initializeServices() {
        // 初始化所有服务
        moduleManager.initializeAll(this)
        
        // 获取房间列表
        val rooms = roomManager.getAllRooms()
        println("已加载 ${rooms.size} 个房间")
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Text(
        text = "RinLink 智能家居控制终端",
        fontSize = 24.sp,
        modifier = modifier
    )
}