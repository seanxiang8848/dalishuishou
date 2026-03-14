const express = require('express');
const { body, validationResult } = require('express-validator');
const db = require('../config/database');

const router = express.Router();

// 获取所有仓库
router.get('/', async (req, res) => {
  try {
    const [warehouses] = await db.query(
      `SELECT w.*, u.name as manager_name, u.phone as manager_phone
      FROM warehouses w
      LEFT JOIN users u ON w.manager_id = u.id
      WHERE w.status = 1
      ORDER BY w.created_at DESC`
    );

    res.json({
      success: true,
      data: warehouses
    });

  } catch (error) {
    console.error('获取仓库列表错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 获取仓库详情
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const [warehouses] = await db.query(
      `SELECT w.*, u.name as manager_name, u.phone as manager_phone
      FROM warehouses w
      LEFT JOIN users u ON w.manager_id = u.id
      WHERE w.id = ?`,
      [id]
    );

    if (warehouses.length === 0) {
      return res.status(404).json({ error: '仓库不存在' });
    }

    // 获取该仓库的配送员
    const [deliveryStaff] = await db.query(
      'SELECT id, name, phone, status FROM users WHERE warehouse_id = ? AND role = "delivery" AND status = 1',
      [id]
    );

    // 获取该仓库的配送区域
    const [regions] = await db.query(
      'SELECT * FROM delivery_regions WHERE warehouse_id = ? AND status = 1',
      [id]
    );

    res.json({
      success: true,
      data: {
        ...warehouses[0],
        deliveryStaff,
        regions
      }
    });

  } catch (error) {
    console.error('获取仓库详情错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 创建仓库
router.post('/', [
  body('name').notEmpty().withMessage('仓库名称不能为空'),
  body('address').notEmpty().withMessage('地址不能为空'),
  body('lat').isFloat().withMessage('请输入正确的纬度'),
  body('lng').isFloat().withMessage('请输入正确的经度')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ error: errors.array()[0].msg });
    }

    const { name, address, lat, lng, managerId } = req.body;

    const [result] = await db.query(
      'INSERT INTO warehouses (name, address, lat, lng, manager_id) VALUES (?, ?, ?, ?, ?)',
      [name, address, lat, lng, managerId || null]
    );

    res.status(201).json({
      success: true,
      message: '仓库创建成功',
      data: { warehouseId: result.insertId }
    });

  } catch (error) {
    console.error('创建仓库错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 更新仓库
router.put('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const { name, address, lat, lng, managerId } = req.body;

    const [result] = await db.query(
      'UPDATE warehouses SET name = ?, address = ?, lat = ?, lng = ?, manager_id = ? WHERE id = ?',
      [name, address, lat, lng, managerId, id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: '仓库不存在' });
    }

    res.json({
      success: true,
      message: '仓库更新成功'
    });

  } catch (error) {
    console.error('更新仓库错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 删除仓库（软删除）
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const [result] = await db.query(
      'UPDATE warehouses SET status = 0 WHERE id = ?',
      [id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: '仓库不存在' });
    }

    res.json({
      success: true,
      message: '仓库已删除'
    });

  } catch (error) {
    console.error('删除仓库错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 获取仓库统计
router.get('/:id/stats', async (req, res) => {
  try {
    const { id } = req.params;
    const { date = new Date().toISOString().slice(0, 10) } = req.query;

    // 今日订单统计
    const [todayStats] = await db.query(
      `SELECT 
        COUNT(*) as total,
        SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending,
        SUM(CASE WHEN status = 'assigned' THEN 1 ELSE 0 END) as assigned,
        SUM(CASE WHEN status = 'delivering' THEN 1 ELSE 0 END) as delivering,
        SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed
      FROM orders 
      WHERE warehouse_id = ? AND DATE(created_at) = ?`,
      [id, date]
    );

    // 配送员工作量
    const [deliveryStats] = await db.query(
      `SELECT 
        u.id, u.name,
        COUNT(o.id) as total_orders,
        SUM(CASE WHEN o.status = 'completed' THEN 1 ELSE 0 END) as completed_orders
      FROM users u
      LEFT JOIN orders o ON o.delivery_id = u.id AND DATE(o.created_at) = ?
      WHERE u.warehouse_id = ? AND u.role = 'delivery' AND u.status = 1
      GROUP BY u.id`,
      [date, id]
    );

    res.json({
      success: true,
      data: {
        today: todayStats[0],
        deliveryStaff: deliveryStats
      }
    });

  } catch (error) {
    console.error('获取仓库统计错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

module.exports = router;