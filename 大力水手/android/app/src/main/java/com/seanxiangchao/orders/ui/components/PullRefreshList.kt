package com.seanxiangchao.orders.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seanxiangchao.orders.ui.theme.NavyBlue

/**
 * 带下拉刷新和上拉加载的列表组件
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshList(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    isLoadingMore: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        // 列表内容
        content()

        // 下拉刷新指示器
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = NavyBlue,
            backgroundColor = MaterialTheme.colorScheme.surface
        )

        // 上拉加载更多指示器
        if (isLoadingMore) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    color = NavyBlue,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

/**
 * 空列表视图
 */
@Composable
fun EmptyListView(
    message: String = "暂无数据",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

/**
 * 加载更多底部组件
 */
@Composable
fun LoadMoreFooter(
    isLoading: Boolean,
    hasMore: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = NavyBlue,
                    strokeWidth = 2.dp
                )
            }
            !hasMore -> {
                Text(
                    text = "没有更多了",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            else -> {
                Text(
                    text = "上拉加载更多",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
