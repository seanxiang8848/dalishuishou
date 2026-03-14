package com.seanxiangchao.orders.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.*
import com.seanxiangchao.orders.data.model.Order

/**
 * 高德地图组件
 */
@Composable
fun AMapView(
    modifier: Modifier = Modifier,
    onMapReady: (AMap) -> Unit = {},
    onLocationUpdate: (LatLng) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    ) { view ->
        view.onCreate(null)
        view.getMapAsync { aMap ->
            // 配置地图
            aMap.apply {
                // 显示定位按钮
                uiSettings.isMyLocationButtonEnabled = true
                uiSettings.isZoomControlsEnabled = true
                
                // 启用定位层
                isMyLocationEnabled = true
                
                // 移动到当前位置
                moveCamera(CameraUpdateFactory.zoomTo(15f))
            }
            
            onMapReady(aMap)
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }
}

/**
 * 路线规划组件
 */
@Composable
fun RoutePlanningMap(
    startPoint: LatLng,
    endPoint: LatLng,
    waypoints: List<LatLng> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    ) { view ->
        view.onCreate(null)
        view.getMapAsync { aMap ->
            // 添加起点标记
            aMap.addMarker(
                MarkerOptions()
                    .position(startPoint)
                    .title("起点")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            // 添加终点标记
            aMap.addMarker(
                MarkerOptions()
                    .position(endPoint)
                    .title("终点")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            // 添加途经点
            waypoints.forEachIndexed { index, point ->
                aMap.addMarker(
                    MarkerOptions()
                        .position(point)
                        .title("途经点${index + 1}")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
            }

            // 调整视野显示所有点
            val boundsBuilder = LatLngBounds.builder()
            boundsBuilder.include(startPoint)
            boundsBuilder.include(endPoint)
            waypoints.forEach { boundsBuilder.include(it) }
            
            aMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100)
            )
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }
}

/**
 * 创建带数字的标记图标
 */
fun createNumberMarker(context: android.content.Context, number: Int): BitmapDescriptor {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = Color.parseColor("#1E3A5F") // NavyBlue
    paint.style = Paint.Style.FILL

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    textPaint.color = Color.WHITE
    textPaint.textSize = 40f
    textPaint.textAlign = Paint.Align.CENTER

    val size = 80
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 画圆
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // 画文字
    val text = number.toString()
    val y = size / 2f - (textPaint.descent() + textPaint.ascent()) / 2
    canvas.drawText(text, size / 2f, y, textPaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
