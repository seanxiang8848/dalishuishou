package com.seanxiangchao.orders.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seanxiangchao.orders.data.model.DeliveryStats
import com.seanxiangchao.orders.data.model.User
import com.seanxiangchao.orders.ui.navigation.Screen
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen
import com.seanxiangchao.orders.ui.viewmodel.AuthViewModel
import com.seanxiangchao.orders.ui.viewmodel.StatsState
import com.seanxiangchao.orders.ui.viewmodel.StatsViewModel
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val statsViewModel: StatsViewModel = viewModel()
    
    val user by authViewModel.user.collectAsStateWithLifecycle()
    val deliveryStats by statsViewModel.deliveryStats.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // 加载配送员统计数据
    LaunchedEffect(user) {
        user?.let {
            if (it.id > 0) {
                statsViewModel.loadDeliveryStats(it.id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人中心") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
                .verticalScroll(scrollState)
        ) {
            // 用户信息卡片
            UserInfoCard(user)

            // 配送统计卡片
            DeliveryStatsCard(deliveryStats)

            // 功能菜单区域
            MenuSection(navController)

            // 设置菜单
            SettingsSection(authViewModel, navController)

            Spacer(modifier = Modifier.height(24.dp))

            // 版本信息
            Text(
                text = "大力水手 v1.0.0",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun UserInfoCard(user: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.name ?: "未知用户",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SpinachGreen,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = when (user?.role) {
                                    "admin" -> "管理员"
                                    "manager" -> "店长"
                                    "delivery" -> "配送员"
                                    else -> "在职"
                                },
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user?.phone?.replaceRange(3, 7, "****") ?: "",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "编辑",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 所属仓库信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warehouse,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user?.warehouseName ?: "未分配仓库",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "工号: ${user?.id?.let { String.format("DLS%03d", it) } ?: "---"}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun DeliveryStatsCard(statsState: StatsState<DeliveryStats>) {
    val stats = (statsState as? StatsState.Success)?.data
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "本月业绩",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                TextButton(onClick = { }) {
                    Text("查看明细", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItemWithIcon(
                    icon = Icons.Default.ShoppingBag,
                    value = stats?.monthOrders?.toString() ?: "0",
                    label = "配送单数",
                    iconBgColor = Color(0xFFE3F2FD),
                    iconColor = NavyBlue
                )
                StatItemWithIcon(
                    icon = Icons.Default.AccountBalanceWallet,
                    value = stats?.monthIncome ?: "¥0",
                    label = "配送收入",
                    iconBgColor = Color(0xFFE8F5E9),
                    iconColor = SpinachGreen
                )
                StatItemWithIcon(
                    icon = Icons.Default.Star,
                    value = String.format("%.1f", stats?.rating ?: 5.0),
                    label = "评分",
                    iconBgColor = Color(0xFFFFF8E1),
                    iconColor = Color(0xFFFFA000)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // 今日数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TodayStatItem("今日订单", stats?.todayOrders?.toString() ?: "0")
                TodayStatItem("今日收入", stats?.todayIncome ?: "¥0.00")
                TodayStatItem("在线时长", "0h")
            }
        }
    }
}

@Composable
fun StatItemWithIcon(
    icon: ImageVector,
    value: String,
    label: String,
    iconBgColor: Color,
    iconColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun TodayStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun MenuSection(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            MenuItem(
                icon = Icons.Default.History,
                iconBgColor = Color(0xFFE3F2FD),
                iconColor = NavyBlue,
                title = "配送历史",
                subtitle = "查看已完成订单",
                onClick = { navController.navigate(Screen.Orders.route) }
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            MenuItem(
                icon = Icons.Default.Map,
                iconBgColor = Color(0xFFE8F5E9),
                iconColor = SpinachGreen,
                title = "配送范围",
                subtitle = "查看负责配送区域"
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            MenuItem(
                icon = Icons.Default.Schedule,
                iconBgColor = Color(0xFFFFF8E1),
                iconColor = Color(0xFFFFA000),
                title = "工作排班",
                subtitle = "查看排班安排"
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            MenuItem(
                icon = Icons.Default.CardGiftcard,
                iconBgColor = Color(0xFFFCE4EC),
                iconColor = Color(0xFFE91E63),
                title = "奖励中心",
                subtitle = "查看奖励和激励"
            )
        }
    }
}

@Composable
fun SettingsSection(authViewModel: AuthViewModel, navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("确认退出") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("退出")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            MenuItem(
                icon = Icons.Default.Lock,
                iconBgColor = Color(0xFFF5F5F5),
                iconColor = Color.Gray,
                title = "修改密码",
                subtitle = null
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            MenuItem(
                icon = Icons.Default.Notifications,
                iconBgColor = Color(0xFFF5F5F5),
                iconColor = Color.Gray,
                title = "消息通知",
                subtitle = null
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            MenuItem(
                icon = Icons.Default.PhoneAndroid,
                iconBgColor = Color(0xFFF5F5F5),
                iconColor = Color.Gray,
                title = "关于我们",
                subtitle = null
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            MenuItem(
                icon = Icons.AutoMirrored.Filled.Help,
                iconBgColor = Color(0xFFF5F5F5),
                iconColor = Color.Gray,
                title = "帮助与反馈",
                subtitle = null
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            // 退出登录
            MenuItem(
                icon = Icons.Default.ExitToApp,
                iconBgColor = Color(0xFFFFEBEE),
                iconColor = Color.Red,
                title = "退出登录",
                subtitle = null,
                onClick = { showLogoutDialog = true }
            )
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    subtitle: String?,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
