package com.rinzelLink.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 应用状态管理器
 * 管理应用程序的全局状态
 */
@Singleton
class AppStateManager @Inject constructor() {
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()
    
    private val _appTheme = MutableStateFlow(AppTheme.AUTO)
    val appTheme: StateFlow<AppTheme> = _appTheme.asStateFlow()
    
    private val _appLanguage = MutableStateFlow(AppLanguage.ZH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()
    
    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()
    
    fun setInitialized(initialized: Boolean) {
        _isInitialized.value = initialized
    }
    
    fun setOnline(online: Boolean) {
        _isOnline.value = online
    }
    
    fun setCurrentUser(userId: String?) {
        _currentUser.value = userId
    }
    
    fun setAppTheme(theme: AppTheme) {
        _appTheme.value = theme
    }
    
    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }
    
    fun setFirstLaunch(firstLaunch: Boolean) {
        _isFirstLaunch.value = firstLaunch
    }
    
    fun getAppVersion(): String {
        return "1.0.0"
    }
    
    fun getBuildNumber(): Int {
        return 1
    }
}

enum class AppTheme {
    LIGHT,
    DARK,
    AUTO
}

enum class AppLanguage {
    ZH,
    EN,
    JA,
    KO
}
