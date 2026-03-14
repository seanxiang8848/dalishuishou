package com.seanxiangchao.orders.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seanxiangchao.orders.data.model.Order
import com.seanxiangchao.orders.ui.components.OrderListShimmer
import com.seanxiangchao.orders.ui.components.PullRefreshList
import com.seanxiangchao.orders.ui.components.LoadMoreFooter
import com.seanxiangchao.orders.ui.navigation.Screen
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen
import com.seanxiangchao.orders.ui.viewmodel.AuthViewModel
import com.seanxiangchao.orders.ui.viewmodel.OrdersState
import com.seanxiangchao.orders.ui.viewmodel.OrdersViewModel
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val ordersViewModel: OrdersViewModel = viewModel()
    
    val ordersState by ordersViewModel.ordersState.collectAsStateWithLifecycle()
    val user by authViewModel.user.collectAsStateWithLifecycle()
    
    val statusTabs = listOf(
        "待派单" to "pending",
        "待出库" to "assigned", 
        "配送中" to "delivering",
        "待退款" to "refunding",
        "已送达" to "completed",
        "已核销" to "verified",
        "未完" to "unfinished"
    )
    var selectedStatusTab by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }

    // 加载订单列表
    LaunchedEffect(selectedStatusTab) {
        val status = if (selectedStatusTab == 0) null else statusTabs[selectedStatusTab].second
        ordersViewModel.loadOrders(status = status)
    }

    // 下拉刷新处理
    fun refresh() {
        isRefreshing = true
        val status = if (selectedStatusTab == 0) null else statusTabs[selectedStatusTab].second
        ordersViewModel.loadOrders(status = status)
        isRefreshing = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "菜单")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.DateRange, contentDescription = "日历")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
                    selected = true,
                    onClick = { }
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
                    selected = false,
                    onClick = { navController.navigate(Screen.More.route) }
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
            // 状态筛选标签
            ScrollableTabRow(
                selectedTabIndex = selectedStatusTab,
                containerColor = Color.White,
                contentColor = NavyBlue,
                edgePadding = 0.dp
            ) {
                statusTabs.forEachIndexed { index, (title, _) ->
                    val count = if (index == 0) {
                        (ordersState as? OrdersState.Success)?.data?.pagination?.total?.toString() ?: ""
                    } else ""
                    Tab(
                        selected = selectedStatusTab == index,
                        onClick = { selectedStatusTab = index },
                        text = {
                            Text(
                                text = if (count.isNotEmpty()) "$title $count" else title,
                                color = if (selectedStatusTab == index) NavyBlue else Color.Gray
                            )
                        }
                    )
                }
            }

            // 筛选条件栏
            FilterBar()

            // 分页控制
            PaginationControl(ordersState)

            // 订单列表
            when (ordersState) {
                is OrdersState.Loading -> {
                    // 使用骨架屏代替简单的加载动画
                    OrderListShimmer(count = 3)
                }
                is OrdersState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (ordersState as OrdersState.Error).message,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { refresh() }) {
                                Text("重试")
                            }
                        }
                    }
                }
                is OrdersState.Success -> {
                    val ordersData = (ordersState as OrdersState.Success).data
                    val orders = ordersData.list
                    val pagination = ordersData.pagination
                    
                    PullRefreshList(
                        isRefreshing = isRefreshing,
                        onRefresh = { refresh() },
                        isLoadingMore = false,
                        onLoadMore = {
                            if (pagination.page < pagination.pages) {
                                ordersViewModel.loadMoreOrders(
                                    status = if (selectedStatusTab == 0) null else statusTabs[selectedStatusTab].second
                                )
                            }
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(orders) { order ->
                                OrderItemCard(
                                    order = order,
                                    onClick = {
                                        navController.navigate(Screen.OrderDetail.createRoute(order.id))
                                    }
                                )
                            }
                            
                            // 加载更多底部
                            if (orders.isNotEmpty()) {
                                item {
                                    LoadMoreFooter(
                                        isLoading = false,
                                        hasMore = pagination.page < pagination.pages
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("加载中...", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterDropdown(text = "时间")
        FilterDropdown(text = "渠道")
        FilterDropdown(text = "关键字")
    }
}

@Composable
fun FilterDropdown(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = Color.DarkGray, fontSize = 14.sp)
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
    }
}

@Composable
fun PaginationControl(ordersState: OrdersState) {
    val pagination = (ordersState as? OrdersState.Success)?.data?.pagination
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F7FA))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(20.dp),
            enabled = pagination?.page ?: 1 > 1
        ) {
            Text("上一页")
        }

        Text(
            text = "${pagination?.page ?: 1} / ${pagination?.pages ?: 1}",
            color = NavyBlue,
            fontWeight = FontWeight.Bold
        )

        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(20.dp),
            enabled = (pagination?.page ?: 1) < (pagination?.pages ?: 1)
        ) {
            Text("下一页")
        }
    }
}

@Composable
fun OrderItemCard(order: Order, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 顶部信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = { },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = order.orderNo,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
                Text(
                    text = "押${order.deposit} 欠${order.oweOrder} 欠票:${order.oweTicket} 欠¥${order.oweAmount}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 仓库和时间
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.warehouseName,
                    fontSize = 16.sp,
                    color = NavyBlue,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = order.createTime.substring(0, 16), // 截取到分钟
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 客户信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.customerName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "(${order.orderCount}次)",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "|",
                    color = Color.LightGray
                )
                Text(
                    text = order.customerPhone,
                    fontSize = 14.sp,
                    color = NavyBlue,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "打电话",
                    tint = NavyBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 地址
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.address,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "定位",
                    tint = NavyBlue,
                    modifier = Modifier.size(20.dp)
                )
                TextButton(onClick = { }) {
                    Text("复制", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 商品信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 商品图片占位
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productName,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    if (!order.productTag.isNullOrEmpty()) {
                        Row {
                            Surface(
                                color = Color(0xFFFFE4E1),
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Text(
                                    text = order.productTag,
                                    fontSize = 10.sp,
                                    color = Color.Red,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "¥${order.price}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Text(
                    text = "x${order.quantity}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 金额信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!order.channel.isNullOrEmpty()) {
                        Surface(
                            color = Color(0xFFFFE4E1),
                            shape = RoundedCornerShape(2.dp)
                        ) {
                            Text(
                                text = order.channel,
                                fontSize = 10.sp,
                                color = Color.Red,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "总价:¥${order.totalPrice}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "需收款: ",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "¥${order.receivable}",
                        fontSize = 16.sp,
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 备注
            if (!order.remark.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "订单备注: ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = order.remark,
                        fontSize = 12.sp,
                        color = Color(0xFFFF6B6B)
                    )
                }
            }

            // 商家备注
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "商家备注: ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 配送工和收入
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "配送工: ${order.deliveryName ?: "待分配"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "配送收入: ¥${order.deliveryIncome}",
                    fontSize = 12.sp,
                    color = NavyBlue
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("更多", fontSize = 12.sp)
                }
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("派单", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("取消", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("回退", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("刷新", fontSize = 12.sp)
                }
            }
        }
    }
}
