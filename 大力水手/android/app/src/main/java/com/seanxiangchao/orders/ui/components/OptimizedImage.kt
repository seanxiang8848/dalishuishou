package com.seanxiangchao.orders.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.seanxiangchao.orders.R
import com.seanxiangchao.orders.ui.theme.NavyBlue

/**
 * 优化的图片加载组件
 * 支持占位图、错误处理、加载动画
 */
@Composable
fun OptimizedImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.ic_logo,
    error: Int = R.drawable.ic_logo,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (imageUrl.isNullOrEmpty()) {
        // 显示占位图
        Image(
            painter = painterResource(id = placeholder),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true) // 淡入动画
                .crossfade(300)  // 300ms淡入
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            loading = {
                // 加载中显示进度条
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = NavyBlue,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                // 加载失败显示错误图
                Image(
                    painter = painterResource(id = error),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        )
    }
}

/**
 * 带缓存优化的图片加载
 * 适用于列表中的图片
 */
@Composable
fun CachedImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .memoryCacheKey(imageUrl)  // 内存缓存
            .diskCacheKey(imageUrl)    // 磁盘缓存
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = painterResource(R.drawable.ic_logo),
        error = painterResource(R.drawable.ic_logo)
    )
}
