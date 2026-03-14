# 大力水手 - API 接口文档

## 📋 接口基础信息

- **Base URL**: `http://seanxiangchao.top/api`
- **Content-Type**: `application/json`
- **认证方式**: Bearer Token (JWT)

---

## 🔐 认证接口

### 1. 用户登录
```http
POST /auth/login
```

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "your_password"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": 1,
      "phone": "13800138000",
      "name": "张三",
      "role": "delivery",
      "warehouseId": 1,
      "avatar": null
    }
  }
}
```

---

### 2. 用户注册
```http
POST /auth/register
```

**请求参数**:
```json
{
  "phone": "13900139000",
  "password": "password123",
  "name": "李四",
  "role": "delivery",
  "warehouseId": 1
}
```

---

### 3. 获取当前用户信息
```http
GET /auth/profile
Authorization: Bearer {token}
```

---

### 4. 修改密码
```http
POST /auth/change-password
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "oldPassword": "old_pass",
  "newPassword": "new_pass"
}
```

---

## 📦 订单接口

### 5. 获取订单列表
```http
GET /orders?page=1&limit=20&status=pending&warehouseId=1
Authorization: Bearer {token}
```

**查询参数**:
- `page` - 页码，默认1
- `limit` - 每页数量，默认20
- `status` - 状态筛选 (pending/assigned/delivering/completed/cancelled)
- `warehouseId` - 仓库ID筛选
- `deliveryId` - 配送员ID筛选
- `keyword` - 关键词搜索（订单号/客户名/手机号）
- `startDate` - 开始日期 (YYYY-MM-DD)
- `endDate` - 结束日期 (YYYY-MM-DD)

**响应示例**:
```json
{
  "success": true,
  "data": {
    "list": [...],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 150,
      "pages": 8
    }
  }
}
```

---

### 6. 获取订单详情
```http
GET /orders/{id}
Authorization: Bearer {token}
```

---

### 7. 创建订单（手动录入）
```http
POST /orders
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "customerName": "张三",
  "customerPhone": "13800138000",
  "address": "成都市锦江区XX路XX号",
  "lat": 30.6586,
  "lng": 104.0648,
  "productName": "桶装水",
  "quantity": 2,
  "remark": "请送到前台",
  "warehouseId": 1
}
```

---

### 8. 分配订单给配送员
```http
POST /orders/{id}/assign
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "deliveryId": 2,
  "warehouseId": 1
}
```

---

### 9. 更新订单状态
```http
POST /orders/{id}/status
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "status": "pickup",        // pickup:开始配送, deliver:完成配送, cancel:取消
  "locationLat": 30.6586,
  "locationLng": 104.0648,
  "photoUrl": "https://...",
  "note": "已送达"
}
```

---

### 10. 获取配送员当前订单
```http
GET /orders/delivery/current?deliveryId=1
Authorization: Bearer {token}
```

---

### 11. 批量分配订单
```http
POST /orders/batch-assign
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "orderIds": [1, 2, 3],
  "deliveryId": 2,
  "warehouseId": 1
}
```

---

## 🏭 仓库接口

### 12. 获取仓库列表
```http
GET /warehouses
Authorization: Bearer {token}
```

---

### 13. 获取仓库详情
```http
GET /warehouses/{id}
Authorization: Bearer {token}
```

---

### 14. 创建仓库
```http
POST /warehouses
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "name": "成都一号仓库",
  "address": "成都市武侯区XX路XX号",
  "lat": 30.6586,
  "lng": 104.0648,
  "managerId": 1
}
```

---

### 15. 更新仓库
```http
PUT /warehouses/{id}
Authorization: Bearer {token}
```

---

### 16. 删除仓库
```http
DELETE /warehouses/{id}
Authorization: Bearer {token}
```

---

### 17. 获取仓库统计
```http
GET /warehouses/{id}/stats?date=2024-03-13
Authorization: Bearer {token}
```

---

## 🚚 配送员接口

### 18. 获取配送员列表
```http
GET /delivery/staff?warehouseId=1
Authorization: Bearer {token}
```

---

### 19. 更新配送员位置
```http
POST /delivery/location
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "lat": 30.6586,
  "lng": 104.0648
}
```

---

### 20. 获取今日统计
```http
GET /delivery/stats/today?deliveryId=1
Authorization: Bearer {token}
```

---

### 21. 获取配送历史
```http
GET /delivery/history?deliveryId=1&page=1&limit=20
Authorization: Bearer {token}
```

---

## 📍 配送区域接口

### 22. 获取区域列表
```http
GET /regions?warehouseId=1
Authorization: Bearer {token}
```

---

### 23. 创建配送区域
```http
POST /regions
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "warehouseId": 1,
  "name": "锦江区片区",
  "boundary": "[[30.65,104.06],[30.66,104.07],...]",
  "defaultDeliveryId": 2
}
```

---

### 24. 地址匹配区域
```http
POST /regions/match
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "lat": 30.6586,
  "lng": 104.0648
}
```

---

## 🎵 抖音对接接口

### 25. 获取授权URL
```http
GET /douyin/auth-url
```

---

### 26. 授权回调
```http
GET /douyin/callback?code=xxx
```

---

### 27. 同步抖音订单
```http
POST /douyin/sync-orders
Authorization: Bearer {token}
```

---

### 28. 获取抖音订单列表
```http
GET /douyin/orders?page=1&limit=20
Authorization: Bearer {token}
```

---

## 📱 推送接口

### 29. 发送推送通知
```http
POST /push/send
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "type": "notification",      // notification: 通知栏, message: 透传消息
  "targetType": "alias",       // alias: 别名(用户), tag: 标签(仓库), all: 广播
  "target": 1,                 // 目标值(userId/warehouseId)
  "title": "新订单",
  "content": "您有新订单待配送",
  "extras": {
    "type": "new_order",
    "orderId": "12345"
  }
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "推送发送成功",
  "data": {
    "pushId": "123456789",
    "target": "alias_1"
  }
}
```

---

### 30. 发送新订单推送
```http
POST /push/new-order
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "deliveryId": 1,
  "orderId": "12345",
  "orderNo": "U123456",
  "customerName": "张三",
  "address": "成都市锦江区..."
}
```

---

### 31. 发送订单分配推送
```http
POST /push/order-assigned
Authorization: Bearer {token}
```

---

### 32. 发送订单取消推送
```http
POST /push/order-cancelled
Authorization: Bearer {token}
```

---

### 33. 广播系统通知
```http
POST /push/broadcast
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "title": "系统维护通知",
  "content": "今晚22:00-23:00系统维护",
  "extras": {}
}
```

---

### 34. 获取推送记录
```http
GET /push/logs?page=1&limit=20
Authorization: Bearer {token}
```

---

## 📊 数据模型

### 订单状态
| 状态 | 说明 |
|-----|------|
| `pending` | 待分配 |
| `assigned` | 已分配，待配送 |
| `delivering` | 配送中 |
| `completed` | 已完成 |
| `cancelled` | 已取消 |

### 用户角色
| 角色 | 说明 |
|-----|------|
| `admin` | 系统管理员 |
| `warehouse_manager` | 仓库负责人 |
| `delivery` | 配送员 |

---

## 🔢 错误码

| 状态码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token无效）|
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突（如手机号已注册）|
| 500 | 服务器内部错误 |
