const express = require('express');
const { body, validationResult } = require('express-validator');
const db = require('../config/database');

const router = express.Router();

// 获取所有配送员
router.get('/staff', async (req, res) => {
  try {
    const { warehouseId, status = 1 } = req.query;
    
    let sql = `
      SELECT u.*, w.name as warehouse_name
      FROM users u
      LEFT JOIN warehouses w ON u.warehouse_id = w.id
      WHERE u.role = 'delivery'
    `;
    const params = [];

    if (warehouseId) {
      sql += ' AND u.warehouse_id = ?';
      params.push(warehouseId);
    }
    if (status !== undefined) {
      sql += ' AND u.status = ?';
      params.push(status);
    }

    sql += ' ORDER BY u.created_at DESC';

    const [staff] = await db.query(sql, params);

    res.json({
      success: true,
      data: staff
    });

  } catch (error) {
    console.error('获取配送员列表错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 更新配送员位置
router.post('/location', [
  body('lat').isFloat().withMessage('纬度格式错误'),
  body('lng').isFloat().withMessage('经度格式错误')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ error: errors.array()[0].msg });
    }

    const { lat, lng } = req.body;
    // const deliveryId = req.user.userId;
    const deliveryId = req.body.deliveryId || 1;

    // 获取当前配送中的所有订单
    const [orders] = await db.query(
      'SELECT id FROM orders WHERE delivery_id = ? AND status = "delivering"',
      [deliveryId]
    );

    // 记录位置日志（关联到配送中的订单）
    for (const order of orders) {
      await db.query(
        `INSERT INTO delivery_logs (order_id, delivery_id, action, location_lat, location_lng) 
         VALUES (?, ?, 'location', ?, ?)`,
        [order.id, deliveryId, lat, lng]
      );
    }

    res.json({
      success: true,
      message: '位置更新成功',
      data: { updatedOrders: orders.length }
    });

  } catch (error) {
    console.error('更新位置错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 获取配送员今日统计
router.get('/stats/today', async (req, res) => {
  try {
    // const deliveryId = req.user.userId;
    const deliveryId = req.query.deliveryId || 1;
    const today = new Date().toISOString().slice(0, 10);

    // 今日订单统计
    const [stats] = await db.query(
      `SELECT 
        COUNT(*) as total,
        SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed,
        SUM(CASE WHEN status IN ('assigned', 'delivering') THEN 1 ELSE 0 END) as inProgress
      FROM orders 
      WHERE delivery_id = ? AND DATE(created_at) = ?`,
      [deliveryId, today]
    );

    // 待配送订单列表
    const [pendingOrders] = await db.query(
      `SELECT o.*, w.name as warehouse_name, w.address as warehouse_address
      FROM orders o
      LEFT JOIN warehouses w ON o.warehouse_id = w.id
      WHERE o.delivery_id = ? AND o.status IN ('assigned', 'delivering')
      ORDER BY o.assigned_at ASC`,
      [deliveryId]
    );

    res.json({
      success: true,
      data: {
        stats: stats[0],
        pendingOrders
      }
    });

  } catch (error) {
    console.error('获取配送统计错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 获取配送历史
router.get('/history', async (req, res) => {
  try {
    // const deliveryId = req.user.userId;
    const deliveryId = req.query.deliveryId || 1;
    const { page = 1, limit = 20 } = req.query;
    const offset = (parseInt(page) - 1) * parseInt(limit);

    const [orders] = await db.query(
      `SELECT o.*, w.name as warehouse_name
      FROM orders o
      LEFT JOIN warehouses w ON o.warehouse_id = w.id
      WHERE o.delivery_id = ? AND o.status IN ('completed', 'cancelled')
      ORDER BY o.updated_at DESC
      LIMIT ? OFFSET ?`,
      [deliveryId, parseInt(limit), offset]
    );

    const [countResult] = await db.query(
      'SELECT COUNT(*) as total FROM orders WHERE delivery_id = ? AND status IN ("completed", "cancelled")',
      [deliveryId]
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
    console.error('获取配送历史错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

module.exports = router;