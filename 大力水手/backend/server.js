require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');

// 路由导入
const authRoutes = require('./routes/auth');
const orderRoutes = require('./routes/orders');
const warehouseRoutes = require('./routes/warehouses');
const deliveryRoutes = require('./routes/delivery');
const regionRoutes = require('./routes/regions');
const douyinRoutes = require('./routes/douyin');
const pushRoutes = require('./routes/push');

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(helmet());
app.use(cors());
app.use(morgan('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 静态文件（上传的图片）
app.use('/uploads', express.static('uploads'));

// 健康检查
app.get('/health', (req, res) => {
  res.json({ 
    status: 'ok', 
    timestamp: new Date().toISOString(),
    service: '大力水手 API'
  });
});

// API路由
app.use('/api/auth', authRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/warehouses', warehouseRoutes);
app.use('/api/delivery', deliveryRoutes);
app.use('/api/regions', regionRoutes);
app.use('/api/douyin', douyinRoutes);
app.use('/api/push', pushRoutes);

// 404处理
app.use((req, res) => {
  res.status(404).json({ error: '接口不存在' });
});

// 错误处理
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ 
    error: '服务器内部错误',
    message: process.env.NODE_ENV === 'development' ? err.message : undefined
  });
});

app.listen(PORT, () => {
  console.log(`🚀 大力水手 API 服务已启动`);
  console.log(`📡 端口: ${PORT}`);
  console.log(`🌐 访问: http://localhost:${PORT}/health`);
});

module.exports = app;