package com.seanxiangchao.orders.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.seanxiangchao.orders.data.model.OrderTimeline
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen
import com.seanxiangchao.orders.ui.components.ShimmerEffect
import com.seanxiangchao.orders.ui.viewmodel.AuthViewModel
import com.seanxiangchao.orders.ui.viewmodel.OrderDetailState
import com.seanxiangchao.orders.ui.viewmodel.OrdersViewModel
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: String) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val ordersViewModel: OrdersViewModel = viewModel()
    
    val orderDetailState by ordersViewModel.orderDetailState.collectAsStateWithLifecycle()
    val user by authViewModel.user.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // 加载订单详情
    LaunchedEffect(orderId) {
        ordersViewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            when (orderDetailState) {
                is OrderDetailState.Success -> {
                    val order = (orderDetailState as OrderDetailState.Success).order
                    OrderDetailBottomBar(
                        order = order,
                        onAssign = { /* TODO: 显示派单对话框 */ },
                        onCancel = { ordersViewModel.updateOrderStatus(order.id, "cancel") },
                        onRefresh = { ordersViewModel.loadOrderDetail(order.id) }
                    )
                }
                else -> { }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            when (orderDetailState) {
                is OrderDetailState.Loading -> {
                    OrderDetailSkeleton()
                }
                is OrderDetailState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (orderDetailState as OrderDetailState.Error).message,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { ordersViewModel.loadOrderDetail(orderId) }) {
                                Text("重试")
                            }
                        }
                    }
                }
                is OrderDetailState.Success -> {
                    val order = (orderDetailState as OrderDetailState.Success).order
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 订单状态卡片
                        OrderStatusCard(order)

                        // 客户信息卡片
                        CustomerInfoCard(order)

                        // 商品信息卡片
                        ProductInfoCard(order)

                        // 金额信息卡片
                        AmountInfoCard(order)

                        // 配送信息卡片
                        DeliveryInfoCard(order)

                        // 订单时间线
                        OrderTimelineCard(order)

                        Spacer(modifier = Modifier.height(80.dp))
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
fun OrderStatusCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
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
                Column {
                    Text(
                        text = order.orderNo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.createTime,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Surface(
                    color = when (order.status) {
                        "pending" -> Color(0xFFFF6B6B)
                        "assigned" -> Color(0xFFFFA726)
                        "delivering" -> Color(0xFF42A5F5)
                        "completed" -> SpinachGreen
                        "cancelled" -> Color.Gray
                        else -> NavyBlue
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = when (order.status) {
                            "pending" -> "待派单"
                            "assigned" -> "待出库"
                            "delivering" -> "配送中"
                            "completed" -> "已送达"
                            "cancelled" -> "已取消"
                            else -> order.status
                        },
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusStatItem("押", order.deposit.toString(), Color(0xFF4CAF50))
                StatusStatItem("欠单", order.oweOrder.toString(), Color(0xFFFF9800))
                StatusStatItem("欠票", order.oweTicket.toString(), Color(0xFF2196F3))
                StatusStatItem("欠¥", order.oweAmount, Color(0xFFF44336))
            }
        }
    }
}

@Composable
fun StatusStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
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
fun CustomerInfoCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 客户名称和下单次数
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.customerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${order.orderCount}次下单",
                        fontSize = 11.sp,
                        color = NavyBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 电话
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.customerPhone,
                    fontSize = 15.sp,
                    color = NavyBlue,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(36.dp)
                        .background(SpinachGreen, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "打电话",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 地址
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.address,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "复制",
                        tint = NavyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 仓库
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warehouse,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.warehouseName,
                    fontSize = 14.sp,
                    color = NavyBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProductInfoCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "商品信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 商品项
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 商品图片占位
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color(0xFFE8F4F8), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = NavyBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productName,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "¥${order.price}",
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "x${order.quantity}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 渠道标签
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!order.channel.isNullOrEmpty()) {
                    Surface(
                        color = Color(0xFFFFE4E1),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = order.channel,
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "共${order.quantity}件商品",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AmountInfoCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "金额明细",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 金额明细行
            AmountRow("商品总价", "¥${order.totalPrice}", Color.DarkGray)
            AmountRow("押金", "¥0.00", Color.Gray)
            AmountRow("优惠", "-¥0.00", Color(0xFF4CAF50))

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 需收款
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "需收款",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Text(
                    text = "¥${order.receivable}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 配送收入
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "配送收入",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "¥${order.deliveryIncome}",
                    fontSize = 14.sp,
                    color = NavyBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AmountRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor
        )
    }
}

@Composable
fun DeliveryInfoCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "配送信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 配送工
            InfoRow("配送工", order.deliveryName ?: "待分配")

            // 期望送达
            InfoRow("期望送达", "尽快送达")

            // 订单备注
            if (!order.remark.isNullOrEmpty()) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "订单备注",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = order.remark,
                            fontSize = 14.sp,
                            color = Color(0xFFFF6B00),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // 商家备注
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "商家备注",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = order.merchantRemark ?: "暂无备注",
                    fontSize = 14.sp,
                    color = if (order.merchantRemark.isNullOrEmpty()) Color.Gray else Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OrderTimelineCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "订单状态",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 时间线
            val timelines = order.timeline ?: generateDefaultTimeline(order)
            timelines.forEachIndexed { index, timeline ->
                TimelineItem(
                    time = timeline.time,
                    title = timeline.title,
                    subtitle = timeline.subtitle,
                    isFirst = index == 0,
                    isLast = index == timelines.lastIndex,
                    isActive = timeline.active
                )
            }
        }
    }
}

// 生成默认时间线
private fun generateDefaultTimeline(order: Order): List<OrderTimeline> {
    return listOf(
        OrderTimeline(
            time = order.createTime.substring(11, 16),
            title = "订单创建",
            subtitle = "用户下单成功",
            active = true
        ),
        OrderTimeline(
            time = "--:--",
            title = when (order.status) {
                "pending" -> "等待派单"
                "assigned" -> "已分配"
                "delivering" -> "配送中"
                "completed" -> "已送达"
                else -> "处理中"
            },
            subtitle = when (order.status) {
                "pending" -> "等待仓库分配配送员"
                "assigned" -> "配送员准备取货"
                "delivering" -> "配送员正在配送"
                "completed" -> "订单已完成"
                else -> ""
            },
            active = order.status != "pending"
        ),
        OrderTimeline(
            time = if (order.status == "completed") order.updateTime?.substring(11, 16) ?: "--:--" else "--:--",
            title = "已完成",
            subtitle = "订单已完成配送",
            active = order.status == "completed"
        )
    )
}

@Composable
fun TimelineItem(
    time: String,
    title: String,
    subtitle: String,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    isActive: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // 左侧时间线
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            // 时间点
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        if (isActive) SpinachGreen else Color.LightGray,
                        CircleShape
                    )
            )
            // 连接线
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(if (isActive) SpinachGreen.copy(alpha = 0.3f) else Color.LightGray)
                )
            }
        }

        // 右侧内容
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 时间
        Text(
            text = time,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderDetailBottomBar(
    order: Order,
    onAssign: () -> Unit,
    onCancel: () -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("更多", fontSize = 13.sp)
            }
            
            // 根据状态显示不同按钮
            when (order.status) {
                "pending" -> {
                    Button(
                        onClick = onAssign,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                    ) {
                        Text("派单", fontSize = 13.sp)
                    }
                }
                "assigned" -> {
                    Button(
                        onClick = { /* TODO: 开始配送 */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpinachGreen)
                    ) {
                        Text("开始配送", fontSize = 13.sp)
                    }
                }
                "delivering" -> {
                    Button(
                        onClick = { /* TODO: 完成配送 */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpinachGreen)
                    ) {
                        Text("完成配送", fontSize = 13.sp)
                    }
                }
                else -> {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("已完成", fontSize = 13.sp)
                    }
                }
            }
            
            if (order.status != "completed" && order.status != "cancelled") {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("取消", fontSize = 13.sp)
                }
            }
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("回退", fontSize = 13.sp)
            }
            
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("刷新", fontSize = 13.sp)
            }
        }
    }
}

/**
 * 订单详情骨架屏
 */
@Composable
fun OrderDetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(12.dp)
    ) {
        // 状态卡片骨架
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShimmerEffect(
                        modifier = Modifier
                            .width(120.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {}
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {}
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ShimmerEffect(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            ) {}
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerEffect(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            ) {}
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 客户信息骨架
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {}
                Spacer(modifier = Modifier.height(12.dp))
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {}
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {}
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 商品信息骨架
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerEffect(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {}
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ShimmerEffect(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {}
                        Spacer(modifier = Modifier.height(8.dp))
                        ShimmerEffect(
                            modifier = Modifier
                                .width(80.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {}
                    }
                }
            }
        }
    }
}
