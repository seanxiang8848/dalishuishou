# 大力水手 - 发布配置清单

## 📦 Android发布准备

### 1. 签名配置
需要创建签名文件：
```bash
keytool -genkey -v -keystore dalishuishou.keystore -alias dalishuishou -keyalg RSA -keysize 2048 -validity 10000
```

### 2. 需要配置的项
- [ ] 创建签名密钥库 (.keystore)
- [ ] 配置 build.gradle 签名
- [ ] 配置 ProGuard 混淆规则
- [ ] 配置多渠道打包（可选）
- [ ] 配置版本号管理

### 3. 上线前检查
- [ ] 关闭调试日志
- [ ] 关闭调试模式
- [ ] 检查权限申请
- [ ] 测试Release包
- [ ] 检查APK大小

## 🔧 后端优化

### 1. 数据库索引优化
```sql
-- 订单表索引
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_warehouse ON orders(warehouse_id);
CREATE INDEX idx_orders_delivery ON orders(delivery_id);
CREATE INDEX idx_orders_created ON orders(created_at);
```

### 2. API性能优化
- [ ] 添加Redis缓存
- [ ] 数据库查询优化
- [ ] 接口响应压缩

## 📊 测试清单

### 功能测试
- [ ] 登录/注册流程
- [ ] 订单列表加载
- [ ] 订单详情查看
- [ ] 派单/取消操作
- [ ] 扫码功能
- [ ] 推送接收

### 性能测试
- [ ] 列表滑动流畅度
- [ ] 图片加载速度
- [ ] 内存占用检查
- [ ] 网络请求优化

### 兼容性测试
- [ ] Android 8.0+ 测试
- [ ] 不同屏幕尺寸
- [ ] 弱网环境测试
