package com.seanxiangchao.orders.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.seanxiangchao.orders.ui.theme.NavyBlue
import com.seanxiangchao.orders.ui.theme.SpinachGreen
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController) {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf<String?>(null) }
    var showManualInput by remember { mutableStateOf(false) }
    var torchEnabled by remember { mutableStateOf(false) }
    
    // ZXing扫码器启动器
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        result.contents?.let {
            scanResult = it
        }
    }

    // 启动扫码
    fun startScan() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(
                ScanOptions.QR_CODE,
                ScanOptions.CODE_128,
                ScanOptions.CODE_39
            )
            setPrompt("将二维码/条形码放入框内扫描")
            setCameraId(0) // 后置摄像头
            setBeepEnabled(true)
            setTorchEnabled(torchEnabled)
            setOrientationLocked(false)
        }
        scanLauncher.launch(options)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("扫码核销") },
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // 扫码结果弹窗
            scanResult?.let { result ->
                ScanResultDialog(
                    result = result,
                    onDismiss = { scanResult = null },
                    onConfirm = {
                        // TODO: 调用核销API
                        scanResult = null
                        // 可以显示核销成功提示
                    }
                )
            }

            // 手动输入弹窗
            if (showManualInput) {
                ManualInputDialog(
                    onDismiss = { showManualInput = false },
                    onConfirm = { orderNo ->
                        scanResult = orderNo
                        showManualInput = false
                    }
                )
            }

            // 主要内容
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 说明文字
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    Text(
                        text = "点击按钮开始扫码",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 扫码图标
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 操作按钮区域
                ScanBottomBar(
                    torchEnabled = torchEnabled,
                    onTorchToggle = { torchEnabled = !torchEnabled },
                    onScanClick = { startScan() },
                    onInputOrderNo = { showManualInput = true }
                )
            }
        }
    }
}

@Composable
fun ScanBottomBar(
    torchEnabled: Boolean,
    onTorchToggle: () -> Unit,
    onScanClick: () -> Unit,
    onInputOrderNo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(24.dp)
    ) {
        // 主要扫码按钮
        Button(
            onClick = onScanClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SpinachGreen)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "开始扫码",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 功能按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 手电筒
            ScanActionButton(
                icon = if (torchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                label = if (torchEnabled) "关闭灯光" else "打开灯光",
                onClick = onTorchToggle
            )

            // 手动输入
            ScanActionButton(
                icon = Icons.Default.Edit,
                label = "手动输入",
                onClick = onInputOrderNo
            )

            // 核销记录
            ScanActionButton(
                icon = Icons.Default.History,
                label = "核销记录",
                onClick = { }
            )
        }
    }
}

@Composable
fun ScanActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ScanResultDialog(
    result: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SpinachGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("扫描成功")
            }
        },
        text = {
            Column {
                Text(
                    text = "订单号：$result",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "确认要核销该订单吗？",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = SpinachGreen)
            ) {
                Text("确认核销")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 手动输入订单号对话框
@Composable
fun ManualInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var orderNo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("手动输入订单号") },
        text = {
            OutlinedTextField(
                value = orderNo,
                onValueChange = { orderNo = it },
                label = { Text("订单号") },
                placeholder = { Text("请输入订单号") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(orderNo) },
                enabled = orderNo.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
