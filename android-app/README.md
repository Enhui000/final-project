# Android 客户端 (Jetpack Compose)

该目录包含 Android Studio 可直接导入的 Kotlin / Jetpack Compose 客户端，占位实现了注册登录页、校园地图页、聊天页和设置页，后续可以逐步接入后端接口。

## 主要功能

- **注册 / 登录**：支持用户名+密码登录与注册切换，带有基础的输入校验逻辑。
- **底部导航**：登录后进入包含“校园地图”“群聊”“设置”的底部导航骨架页面。
- **占位内容**：每个页面均预留了待接入后端或地图 SDK 的位置，方便逐步替换。

## 如何运行

1. 使用 Android Studio (Giraffe 以上) 打开 `android-app` 目录。
2. 第一次同步时若缺少 Gradle Wrapper，可以在 IDE 中执行 **Generate Gradle Wrapper** 或在命令行运行：
   ```bash
   gradle wrapper
   ```
   然后重新同步项目。
3. 连接模拟器或真机，选择 `app` 模块的 `MainActivity` 作为启动 Activity，点击运行即可。

> **提示**：目前所有网络请求均为占位，`onAuthSuccess` 中直接切换到主界面。接入真实后端时，可在 `AuthScreen` 的提交按钮点击逻辑里触发 Retrofit/Ktor 等网络请求，再根据响应更新 `errorMessage` 或调用 `onAuthSuccess`。

## 后续扩展建议

- 在 `MapScreen` 中接入高德 / Google Map 组件，替换当前的占位文案。
- 在 `ChatScreen` 中引入消息列表组件和输入框，结合后端 WebSocket/HTTP API。
- 在 `SettingsScreen` 中补充头像、通知、主题等配置，并将“退出登录”与真实的凭证清理逻辑连接。
