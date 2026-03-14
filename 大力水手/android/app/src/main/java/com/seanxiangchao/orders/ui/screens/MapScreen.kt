package com.seanxiangchao.orders.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.amap.api.maps.model.LatLng
import com.seanxiangchao.orders.ui.components.AMapView
import com.seanxiangchao.orders.ui.components.RoutePlanningMap
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var showRoutePlanning by remember { mutableStateOf(true) }
    
    // 示例路线数据
    val startPoint = LatLng(30.6586, 104.0648) // 起点：锦江仓
    val endPoint = LatLng(30.6286, 104.0848)   // 终点
    val waypoints = listOf(
        LatLng(30.6486, 104.0748), // 途经点1
        LatLng(30.6386, 104.0798)  // 途经点2
    )
    
    val waypointsData = listOf(
        Waypoint(
            type = "start",
            address = "柳翠路120号",
            phone = "",
            distance = "",
            duration = "",
            note = ""
        ),
        Waypoint(
            type = "waypoint",
            address = "4楼中间的门8栋-2单-4L中间的门",
            phone = "18135351696",
            distance = "距起点5.3公里",
            duration = "13分钟",
            note = "4楼中间的门8栋-2单"
        ),
        Waypoint(
            type = "end",
            address = "18135351696",
            phone = "",
            distance = "7公里",
            duration = "19分钟",
            note = "2个途经点"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("顺路规划") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 地图区域 - 使用真实高德地图
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                // 高德地图
                RoutePlanningMap(
                    startPoint = startPoint,
                    endPoint = endPoint,
                    waypoints = waypoints,
                    modifier = Modifier.fillMaxSize()
                )

                // 定位按钮
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 刷新定位
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 全览
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(2.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "全览",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("全览", fontSize = 8.sp, color = Color.DarkGray)
                        }
                    }
                }

                // 高德地图水印
                Text(
                    "高德地图",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // 路线规划类型选择
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NavyBlue,
                    onClick = { }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("驾车", color = Color.White, fontSize = 14.sp)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF5F5F5),
                    onClick = { }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("高德推荐", color = Color.DarkGray, fontSize = 14.sp)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 路线总览
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = SpinachGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "7公里 · 19分钟",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Gray
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "● 柳翠路120号",
                        fontSize = 14.sp,
                        color = SpinachGreen
                    )
                    Text(
                        text = " → ",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "● 18135351696 (2个途经点)",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }

            // 编辑时间
            Text(
                text = "编辑于2026/03/14 00:30",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Divider()

            // 途经点列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(waypointsData) { index, waypoint ->
                    WaypointItem(
                        waypoint = waypoint,
                        index = index,
                        isLast = index == waypointsData.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
fun WaypointItem(waypoint: Waypoint, index: Int, isLast: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 左侧标记
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (waypoint.type) {
                "start" -> {
                    // 起点 - 绿色圆点
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(SpinachGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "起",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "waypoint" -> {
                    // 途经点 - 蓝色圆圈带数字
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(NavyBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$index",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "end" -> {
                    // 终点 - 红色圆点
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Red, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "终",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!isLast) {
                // 连接线
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧信息
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = waypoint.address,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            if (waypoint.phone.isNotEmpty()) {
                Text(
                    text = waypoint.phone,
                    fontSize = 14.sp,
                    color = NavyBlue
                )
            }

            if (waypoint.distance.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${waypoint.distance} · ${waypoint.duration}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (waypoint.note.isNotEmpty()) {
                Text(
                    text = waypoint.note,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class Waypoint(
    val type: String, // start, waypoint, end
    val address: String,
    val phone: String,
    val distance: String,
    val duration: String,
    val note: String
)
