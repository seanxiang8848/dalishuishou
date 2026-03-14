package com.seanxiangchao.orders.ui.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*

/**
 * 监听列表滚动到底部
 * 用于实现加载更多功能
 */
@Composable
fun LazyListState.OnBottomReached(
    buffer: Int = 0,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) {
                    onLoadMore()
                }
            }
    }
}

/**
 * 防抖处理
 * 防止频繁触发操作
 */
fun <T> ((T) -> Unit).debounce(
    waitMs: Long = 300L
): (T) -> Unit {
    var debounceJob: kotlinx.coroutines.Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            delay(waitMs)
            this@debounce(param)
        }
    }
}

/**
 * 节流处理
 * 限制操作频率
 */
fun <T> ((T) -> Unit).throttle(
    skipMs: Long = 500L
): (T) -> Unit {
    var lastTime = 0L
    return { param: T ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > skipMs) {
            lastTime = currentTime
            this@throttle(param)
        }
    }
}

/**
 * 根据字符串生成颜色
 * 用于头像背景等
 */
fun generateColorFromString(str: String): androidx.compose.ui.graphics.Color {
    val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFF5B9BD5),
        androidx.compose.ui.graphics.Color(0xFF70AD47),
        androidx.compose.ui.graphics.Color(0xFFFFC000),
        androidx.compose.ui.graphics.Color(0xFFE97132),
        androidx.compose.ui.graphics.Color(0xFF9E480E),
        androidx.compose.ui.graphics.Color(0xFF636363),
        androidx.compose.ui.graphics.Color(0xFF264478),
        androidx.compose.ui.graphics.Color(0xFF9E480E)
    )
    
    val hash = str.hashCode()
    return colors[Math.abs(hash) % colors.size]
}

/**
 * 格式化手机号
 * 13800138000 -> 138****8000
 */
fun formatPhone(phone: String?): String {
    if (phone.isNullOrEmpty() || phone.length != 11) return phone ?: ""
    return phone.replaceRange(3, 7, "****")
}

/**
 * 格式化金额
 * 保留两位小数
 */
fun formatAmount(amount: String?): String {
    if (amount.isNullOrEmpty()) return "¥0.00"
    val num = amount.toDoubleOrNull() ?: 0.0
    return "¥%.2f".format(num)
}

/**
 * 防抖点击修饰符
 */
@Composable
fun Modifier.debounceClick(
    waitMs: Long = 300L,
    onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    
    return this.clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > waitMs) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
