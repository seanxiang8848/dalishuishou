package com.seanxiangchao.orders.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.seanxiangchao.orders.R
import com.seanxiangchao.orders.ui.navigation.Screen
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen

data class MenuItem(
    val icon: ImageVector,
    val label: String,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(navController: NavController) {
    val quickActions = listOf(
        MenuItem(Icons.Default.Person, "我的用户"),
        MenuItem(Icons.Default.BarChart, "配送统计"),
        MenuItem(Icons.Default.ShoppingCart, "进销存"),
        MenuItem(Icons.Default.TrendingUp, "代运营服务")
    )

    val menuItems = listOf(
        MenuItem(Icons.Default.Assignment, "接单库存"),
        MenuItem(Icons.Default.AccountBalance, "现金账户"),
        MenuItem(Icons.Default.People, "我的工人"),
        MenuItem(Icons.Default.WaterDrop, "电子水票"),
        MenuItem(Icons.Default.QrCode, "二维码"),
        MenuItem(Icons.Default.Store, "门店设置"),
        MenuItem(Icons.Default.Assessment, "订单统计"),
        MenuItem(Icons.Default.Delete, "空桶统计"),
        MenuItem(Icons.Default.CheckCircle, "待核销", badge = "99+"),
        MenuItem(Icons.Default.CardMembership, "实体卡"),
        MenuItem(Icons.Default.Payment, "软件续费"),
        MenuItem(Icons.Default.ShoppingBag, "订货商城"),
        MenuItem(Icons.Default.PieChart, "盘点统计"),
        MenuItem(Icons.Default.Share, "推广统计"),
        MenuItem(Icons.Default.Inventory, "商品盘点"),
        MenuItem(Icons.Default.DeleteSweep, "空桶盘点"),
        MenuItem(Icons.Default.History, "旧配送统计"),
        MenuItem(Icons.Default.EditNote, "录入订单"),
        MenuItem(Icons.Default.LocalShipping, "我的配送"),
        MenuItem(Icons.Default.Map, "顺路规划"),
        MenuItem(Icons.Default.CleaningServices, "清除缓存"),
        MenuItem(Icons.Default.WaterDrop, "周期订水"),
        MenuItem(Icons.Default.AccountBalanceWallet, "财务中心"),
        MenuItem(Icons.Default.ShoppingBag, "订货商城(新)"),
        MenuItem(Icons.Default.TrendingUp, "代运营服务")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("更多") },
                actions = {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "消息"
                        )
                        // 消息角标
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
                                fontSize = 8.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("首页") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("订单") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Orders.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
                    label = { Text("商品") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Apps, contentDescription = null) },
                    label = { Text("更多") },
                    selected = true,
                    onClick = { }
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
            // 用户信息卡片
            UserInfoCard()

            // 广告横幅
            AdBanner()

            // 快捷功能
            QuickActionsRow(actions = quickActions)

            // 功能网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(menuItems) { item ->
                    MenuGridItem(item = item)
                }
            }
        }
    }
}

@Composable
fun UserInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(NavyBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "仓",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 用户信息
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "锦江仓",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "未设置",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "杨昌源",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = " | ",
                        color = Color.Gray
                    )
                    Text(
                        text = "店长",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = " | ",
                        color = Color.Gray
                    )
                    Text(
                        text = "18329653045",
                        fontSize = 14.sp,
                        color = NavyBlue
                    )
                }
            }
        }
    }
}

@Composable
fun AdBanner() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 第一个广告
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FD))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(NavyBlue, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Rocket,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "运营赋能，助力增长！",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "一站式代运营",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("立即申请", fontSize = 12.sp)
                }
            }
        }

        // 第二个广告
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFF9800), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "天网接单",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("立即申请", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(actions: List<MenuItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { action ->
            QuickActionItem(icon = action.icon, label = action.label)
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = when (label) {
                "我的用户" -> Color(0xFF64B5F6)
                "配送统计" -> Color(0xFF81C784)
                "进销存" -> Color(0xFFFFB74D)
                else -> Color(0xFF9575CD)
            },
            modifier = Modifier.size(56.dp)
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
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun MenuGridItem(item: MenuItem) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = NavyBlue,
                modifier = Modifier.size(28.dp)
            )
            if (item.badge != null) {
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-4).dp)
                        .background(Color.Red, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = item.badge,
                        color = Color.White,
                        fontSize = 8.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            fontSize = 11.sp,
            color = Color.DarkGray,
            maxLines = 1
        )
    }
}