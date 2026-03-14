package com.seanxiangchao.orders.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seanxiangchao.orders.ui.navigation.Screen
import com.seanxiangchao.orders.ui.components.StatsCardShimmer
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen
import com.seanxiangchao.orders.ui.viewmodel.AuthViewModel
import com.seanxiangchao.orders.ui.viewmodel.StatsState
import com.seanxiangchao.orders.ui.viewmodel.StatsViewModel
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val statsViewModel: StatsViewModel = viewModel()
    
    val user by authViewModel.user.collectAsStateWithLifecycle()
    val dashboardStats by statsViewModel.dashboardStats.collectAsStateWithLifecycle()
    val todayStats by statsViewModel.todayStats.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("今日来电", "刷新成功", "线上收入")

    // 加载统计数据
    LaunchedEffect(Unit) {
        statsViewModel.loadDashboardStats()
        statsViewModel.loadTodayStats()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("首页") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("订单") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(Screen.Orders.route)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
                    label = { Text("商品") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Apps, contentDescription = null) },
                    label = { Text("更多") },
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        navController.navigate(Screen.More.route)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            // 顶部搜索栏
            SearchBar()

            Spacer(modifier = Modifier.height(16.dp))

            // 快捷功能入口
            QuickActionsRow(navController)

            Spacer(modifier = Modifier.height(16.dp))

            // 订单统计卡片 (对接API数据)
            when (dashboardStats) {
                is StatsState.Loading -> {
                    StatsCardShimmer()
                }
                else -> {
                    OrderStatsCard(dashboardStats)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab切换
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color.White,
                contentColor = NavyBlue
            ) {
                subTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTab == index,
                        onClick = { selectedSubTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // 内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                when (selectedSubTab) {
                    0 -> TodayCallContent(todayStats)
                    1 -> RefreshSuccessContent()
                    2 -> OnlineIncomeContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = { },
            placeholder = { Text("输入电话快速下单") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 下单按钮
        TextButton(onClick = { }) {
            Text(
                text = "下单",
                color = NavyBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 消息图标
        Box {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "消息",
                tint = Color.Gray
            )
            // 消息数量角标
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(16.dp)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "99",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionItem(
            icon = Icons.Default.Person,
            label = "我的用户",
            backgroundColor = Color(0xFF5B9BD5)
        )
        QuickActionItem(
            icon = Icons.Default.BarChart,
            label = "配送统计",
            backgroundColor = SpinachGreen,
            onClick = { navController.navigate(Screen.Profile.route) }
        )
        QuickActionItem(
            icon = Icons.Default.ShoppingCart,
            label = "进销存",
            backgroundColor = Color(0xFFFF9800)
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector, 
    label: String, 
    backgroundColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            modifier = Modifier.size(56.dp),
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun OrderStatsCard(statsState: StatsState<com.seanxiangchao.orders.data.model.DashboardStats>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 从API获取数据
            val pendingCount = (statsState as? StatsState.Success)?.data?.pendingCount ?: 0
            val deliveringCount = (statsState as? StatsState.Success)?.data?.deliveringCount ?: 0
            val todayOrders = (statsState as? StatsState.Success)?.data?.todayOrders ?: 0
            val todayAmount = (statsState as? StatsState.Success)?.data?.todayAmount ?: "¥0.00"
            val yesterdayOrders = (statsState as? StatsState.Success)?.data?.yesterdayOrders ?: 0
            val yesterdayAmount = (statsState as? StatsState.Success)?.data?.yesterdayAmount ?: "¥0.00"
            val monthOrders = (statsState as? StatsState.Success)?.data?.monthOrders ?: 0
            val monthAmount = (statsState as? StatsState.Success)?.data?.monthAmount ?: "¥0.00"
            
            // 订单数量统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItemWithNumber(number = pendingCount.toString(), label = "待派单", color = Color.Red)
                StatItemWithNumber(number = deliveringCount.toString(), label = "配送中", color = NavyBlue)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // 业绩统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PerformanceItem(orders = todayOrders.toString(), amount = todayAmount, label = "今日")
                PerformanceItem(orders = yesterdayOrders.toString(), amount = yesterdayAmount, label = "昨日")
                PerformanceItem(orders = monthOrders.toString(), amount = monthAmount, label = "本月")
            }
        }
    }
}

@Composable
fun StatItemWithNumber(number: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PerformanceItem(orders: String, amount: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$label: ${orders}单",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun TodayCallContent(todayStats: StatsState<com.seanxiangchao.orders.data.model.TodayStats>) {
    val callCount = (todayStats as? StatsState.Success)?.data?.callCount ?: 0
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("今日账号来电: ", color = Color.Gray)
            Text(
                callCount.toString(), 
                color = Color.Red, 
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RefreshSuccessContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("暂无数据", color = Color.Gray)
    }
}

@Composable
fun OnlineIncomeContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("暂无数据", color = Color.Gray)
    }
}
