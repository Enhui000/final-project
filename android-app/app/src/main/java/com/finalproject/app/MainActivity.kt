package com.finalproject.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finalproject.app.ui.theme.FinalProjectTheme

enum class AuthMode(val displayText: String) {
    LOGIN("登录"),
    REGISTER("注册")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    FinalProjectApp()
                }
            }
        }
    }
}

@Composable
fun FinalProjectApp() {
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    var lastSuccessfulUsername by rememberSaveable { mutableStateOf<String?>(null) }

    if (!isAuthenticated) {
        AuthScreen(
            onAuthSuccess = { username ->
                lastSuccessfulUsername = username
                isAuthenticated = true
            }
        )
    } else {
        MainShell(
            onLogout = {
                isAuthenticated = false
            },
            username = lastSuccessfulUsername.orEmpty()
        )
    }
}

@Composable
fun AuthScreen(onAuthSuccess: (String) -> Unit) {
    var authMode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val isRegister = authMode == AuthMode.REGISTER

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Campus Explorer",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthModeToggle(
            selectedMode = authMode,
            onModeChange = { mode ->
                authMode = mode
                errorMessage = null
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = {
                username = it
                errorMessage = null
            },
            singleLine = true,
            label = { Text(text = "用户名") },
            placeholder = { Text(text = "输入用户名") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            singleLine = true,
            label = { Text(text = "密码") },
            placeholder = { Text(text = "输入密码") },
            visualTransformation = PasswordVisualTransformation()
        )
        if (isRegister) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                singleLine = true,
                label = { Text(text = "确认密码") },
                placeholder = { Text(text = "再次输入密码") },
                visualTransformation = PasswordVisualTransformation()
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "请输入用户名和密码"
                    return@Button
                }
                if (isRegister && password != confirmPassword) {
                    errorMessage = "两次密码输入不一致"
                    return@Button
                }
                errorMessage = null
                onAuthSuccess(username)
            },
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(text = if (isRegister) "注册" else "登录", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = {
            authMode = if (isRegister) AuthMode.LOGIN else AuthMode.REGISTER
            errorMessage = null
        }) {
            Text(
                text = if (isRegister) "已有账号？点击登录" else "没有账号？点击注册"
            )
        }
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthModeToggle(selectedMode: AuthMode, onModeChange: (AuthMode) -> Unit) {
    SegmentedButtonRow {
        AuthMode.values().forEach { mode ->
            SegmentedButton(
                selected = selectedMode == mode,
                onClick = { onModeChange(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = mode.ordinal, count = AuthMode.values().size),
                label = { Text(text = mode.displayText) }
            )
        }
    }
}

private enum class MainDestination(val route: String, val label: String) {
    MAP("map", "校园地图"),
    CHAT("chat", "群聊"),
    SETTINGS("settings", "设置")
}

@Composable
fun MainShell(onLogout: () -> Unit, username: String) {
    val navController = rememberNavController()
    val destinations = MainDestination.values().toList()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MainDestination.MAP.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            when (destination) {
                                MainDestination.MAP -> Icon(Icons.Default.Map, contentDescription = null)
                                MainDestination.CHAT -> Icon(Icons.Default.Chat, contentDescription = null)
                                MainDestination.SETTINGS -> Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainDestination.MAP.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainDestination.MAP.route) {
                MapScreen(username = username)
            }
            composable(MainDestination.CHAT.route) {
                ChatScreen(username = username)
            }
            composable(MainDestination.SETTINGS.route) {
                SettingsScreen(onLogout = onLogout, username = username)
            }
        }
    }
}

@Composable
fun MapScreen(username: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "欢迎 ${'$'}username！\n这里将展示校园地图，占位中…",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChatScreen(username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "聊天频道",
            style = MaterialTheme.typography.headlineSmall
        )
        repeat(3) { index ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "群组 ${'$'}{index + 1}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "和同学们讨论课程、活动或校园生活。")
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: connect to backend chat service */ }
        ) {
            Text(text = "开始新的讨论")
        }
    }
}

@Composable
fun SettingsScreen(onLogout: () -> Unit, username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "设置", style = MaterialTheme.typography.headlineSmall)
        Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "当前用户", style = MaterialTheme.typography.titleMedium)
                Text(text = username)
            }
        }
        Button(onClick = onLogout) {
            Text(text = "退出登录")
        }
    }
}
