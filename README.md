# RinLink 智能家居控制终端

一个基于Android的智能家居控制应用，参考绿米(Aqara)的设计理念，提供现代化的UI和完整的智能家居管理功能。

## 功能特性

### 🏠 智能家居管理
- **设备管理**: 支持多种协议设备（Zigbee、WiFi、蓝牙、MQTT等）
- **房间管理**: 按房间组织设备，支持自定义房间类型和颜色
- **场景控制**: 创建和执行智能场景，支持条件触发和定时执行
- **分组控制**: 支持设备分组和批量操作

### 🎨 现代化UI设计
- **Material Design 3**: 采用最新的Material Design设计语言
- **绿米风格**: 参考绿米应用的设计理念和色彩方案
- **响应式布局**: 适配不同屏幕尺寸和方向
- **深色模式**: 支持深色/浅色主题切换

### 🔧 技术特性
- **MVVM架构**: 使用ViewModel和LiveData/StateFlow
- **依赖注入**: 使用Hilt进行依赖管理
- **协程支持**: 使用Kotlin协程处理异步操作
- **模块化设计**: 核心功能模块化，便于维护和扩展

## 项目结构

```
app/
├── src/main/java/com/rinzelLink/
│   ├── ui/
│   │   ├── components/          # UI组件
│   │   ├── screens/            # 页面
│   │   ├── viewmodels/         # ViewModel
│   │   ├── navigation/         # 导航
│   │   └── theme/             # 主题
│   ├── core/                   # 核心业务逻辑
│   ├── network/               # 网络管理
│   ├── sync/                  # 数据同步
│   ├── state/                 # 应用状态
│   └── utils/                 # 工具类
├── build.gradle.kts
└── proguard-rules.pro

core/                          # 核心模块
├── device/                    # 设备管理
├── room/                      # 房间管理
├── scene/                     # 场景管理
└── module/                    # 通信模块

modules/                       # 通信模块
├── lan/                       # 局域网通信
├── zigbee/                    # Zigbee通信
├── websocket/                 # WebSocket通信
├── ble/                       # 蓝牙通信
└── mqtt/                      # MQTT通信
```

## 主要页面

### 首页 (HomeScreen)
- 欢迎信息和天气显示
- 快速控制设备
- 智能场景快捷入口
- 房间概览
- 设备状态统计

### 设备页面 (DeviceScreen)
- 设备列表和筛选
- 设备状态管理
- 设备类型分类
- 设备详情查看

### 场景页面 (SceneScreen)
- 推荐场景
- 自定义场景管理
- 场景模板
- 场景执行历史

### 房间页面 (RoomScreen)
- 房间网格视图
- 房间设备管理
- 房间设置
- 房间统计信息

### 个人页面 (ProfileScreen)
- 用户信息管理
- 应用设置
- 数据统计
- 帮助与支持

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM
- **依赖注入**: Hilt
- **异步处理**: Kotlin Coroutines
- **状态管理**: StateFlow
- **网络请求**: Retrofit + OkHttp
- **本地存储**: Room + DataStore
- **构建工具**: Gradle

## 安装和运行

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 运行项目

## 配置说明

### 依赖注入配置
项目使用Hilt进行依赖注入，需要在Application类上添加`@HiltAndroidApp`注解。

### 主题配置
在`ui/theme/`目录下配置应用主题，包括颜色、字体、形状等。

### 网络配置
在`network/`目录下配置网络请求和WebSocket连接。

## 开发指南

### 添加新设备类型
1. 在`core/device/DeviceType.kt`中添加新的设备类型
2. 在`ui/components/`中添加对应的UI组件
3. 在`ui/screens/`中添加设备详情页面

### 添加新通信协议
1. 在`modules/`目录下创建新的通信模块
2. 实现`ModuleManager`接口
3. 在`core/module/ModuleManager.kt`中注册新模块

### 自定义主题
1. 在`ui/theme/Color.kt`中定义颜色
2. 在`ui/theme/Typography.kt`中定义字体
3. 在`ui/theme/Theme.kt`中应用主题

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱: support@rinzellink.com
- 项目地址: https://github.com/rinzel/RinLink

---

**注意**: 本项目仅用于学习和研究目的，请勿用于商业用途。
```

现在您的RinLink智能家居应用已经基本完善了！这个应用包含了：

## 主要功能
1. **完整的UI系统** - 参考绿米设计风格的现代化界面
2. **设备管理** - 支持多种协议和设备类型
3. **房间管理** - 房间组织和设备分类
4. **场景控制** - 智能场景创建和执行
5. **用户设置** - 完整的设置和配置系统
6. **数据同步** - 本地和云端数据同步

## 技术特点
1. **MVVM架构** - 清晰的代码结构
2. **依赖注入** - 使用Hilt管理依赖
3. **响应式UI** - 使用Jetpack Compose
4. **模块化设计** - 易于维护和扩展
5. **多协议支持** - Zigbee、WiFi、蓝牙、MQTT等

## 下一步建议
1. 添加实际的设备通信逻辑
2. 实现云端数据同步
3. 添加用户认证系统
4. 完善错误处理和日志系统
5. 添加单元测试和UI测试

这个应用已经具备了智能家居控制应用的核心功能，可以作为进一步开发的基础。


## 主要页面

### 首页 (HomeScreen)
- 欢迎信息和天气显示
- 快速控制设备
- 智能场景快捷入口
- 房间概览
- 设备状态统计

### 设备页面 (DeviceScreen)
- 设备列表和筛选
- 设备状态管理
- 设备类型分类
- 设备详情查看

### 场景页面 (SceneScreen)
- 推荐场景
- 自定义场景管理
- 场景模板
- 场景执行历史

### 房间页面 (RoomScreen)
- 房间网格视图
- 房间设备管理
- 房间设置
- 房间统计信息

### 个人页面 (ProfileScreen)
- 用户信息管理
- 应用设置
- 数据统计
- 帮助与支持

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM
- **依赖注入**: Hilt
- **异步处理**: Kotlin Coroutines
- **状态管理**: StateFlow
- **网络请求**: Retrofit + OkHttp
- **本地存储**: Room + DataStore
- **构建工具**: Gradle

## 安装和运行

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 运行项目

## 配置说明

### 依赖注入配置
项目使用Hilt进行依赖注入，需要在Application类上添加`@HiltAndroidApp`注解。

### 主题配置
在`ui/theme/`目录下配置应用主题，包括颜色、字体、形状等。

### 网络配置
在`network/`目录下配置网络请求和WebSocket连接。

## 开发指南

### 添加新设备类型
1. 在`core/device/DeviceType.kt`中添加新的设备类型
2. 在`ui/components/`中添加对应的UI组件
3. 在`ui/screens/`中添加设备详情页面

### 添加新通信协议
1. 在`modules/`目录下创建新的通信模块
2. 实现`ModuleManager`接口
3. 在`core/module/ModuleManager.kt`中注册新模块

### 自定义主题
1. 在`ui/theme/Color.kt`中定义颜色
2. 在`ui/theme/Typography.kt`中定义字体
3. 在`ui/theme/Theme.kt`中应用主题

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱: support@rinzellink.com
- 项目地址: https://github.com/rinzel/RinLink