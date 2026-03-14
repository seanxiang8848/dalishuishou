const express = require('express');
const axios = require('axios');
const db = require('../config/database');

const router = express.Router();

// 抖音开放平台配置（需要申请后填写）
const DOUYIN_CONFIG = {
  appId: process.env.DOUYIN_APP_ID,
  appSecret: process.env.DOUYIN_APP_SECRET,
  baseUrl: 'https://open.douyin.com'
};

// 获取授权URL（引导用户授权）
router.get('/auth-url', (req, res) => {
  const redirectUri = encodeURIComponent(`${req.protocol}://${req.get('host')}/api/douyin/callback`);
  const authUrl = `${DOUYIN_CONFIG.baseUrl}/platform/oauth/connect?client_key=${DOUYIN_CONFIG.appId}&redirect_uri=${redirectUri}&response_type=code&scope=order`;
  
  res.json({
    success: true,
    data: { authUrl }
  });
});

// 授权回调
router.get('/callback', async (req, res) => {
  try {
    const { code } = req.query;
    
    if (!code) {
      return res.status(400).json({ error: '缺少授权码' });
    }

    // 用code换取access_token
    // 注意：实际对接时需要实现
    res.json({
      success: true,
      message: '授权成功（演示模式）',
      data: { code }
    });

  } catch (error) {
    console.error('抖音授权回调错误:', error);
    res.status(500).json({ error: '授权失败' });
  }
});

// 手动同步抖音订单（演示接口）
router.post('/sync-orders', async (req, res) => {
  try {
    // 实际对接时需要调用抖音API获取订单
    // 这里返回演示数据
    
    const mockOrders = [
      {
        platform_order_id: 'DOUYIN_' + Date.now(),
        customer_name: '张三',
        customer_phone: '138****8888',
        address: '上海市静安区XX路XX号',
        product_name: '桶装水 x2',
        quantity: 2,
        remark: '请送到前台'
      }
    ];

    // 插入到订单表
    const insertedOrders = [];
    for (const order of mockOrders) {
      const orderNo = `SDD${new Date().toISOString().slice(0, 10).replace(/-/g, '')}${Math.floor(100000 + Math.random() * 900000)}`;
      
      const [result] = await db.query(
        `INSERT INTO orders 
        (order_no, platform, platform_order_id, customer_name, customer_phone, 
         address, product_name, quantity, remark, status) 
        VALUES (?, 'douyin', ?, ?, ?, ?, ?, ?, ?, 'pending')`,
        [orderNo, order.platform_order_id, order.customer_name, order.customer_phone,
         order.address, order.product_name, order.quantity, order.remark]
      );

      insertedOrders.push({ orderId: result.insertId, orderNo });
    }

    res.json({
      success: true,
      message: `成功同步 ${insertedOrders.length} 条订单`,
      data: { insertedOrders }
    });

  } catch (error) {
    console.error('同步订单错误:', error);
    res.status(500).json({ error: '同步失败' });
  }
});

// 获取抖音订单列表（从本地数据库）
router.get('/orders', async (req, res) => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (parseInt(page) - 1) * parseInt(limit);

    const [orders] = await db.query(
      `SELECT o.*, w.name as warehouse_name, d.name as delivery_name
      FROM orders o
      LEFT JOIN warehouses w ON o.warehouse_id = w.id
      LEFT JOIN users d ON o.delivery_id = d.id
      WHERE o.platform = 'douyin'
      ORDER BY o.created_at DESC
      LIMIT ? OFFSET ?`,
      [parseInt(limit), offset]
    );

    const [countResult] = await db.query(
      'SELECT COUNT(*) as total FROM orders WHERE platform = "douyin"'
    );

    res.json({
      success: true,
      data: {
        list: orders,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total: countResult[0].total
        }
      }
    });

  } catch (error) {
    console.error('获取抖音订单错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// Webhook接收抖音订单推送（预留）
router.post('/webhook', async (req, res) => {
  try {
    // 验证签名
    // 处理订单推送
    // 存入数据库
    
    res.json({ success: true });
  } catch (error) {
    console.error('Webhook处理错误:', error);
    res.status(500).json({ error: '处理失败' });
  }
});

module.exports = router;