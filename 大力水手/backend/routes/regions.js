const express = require('express');
const { body } = require('express-validator');
const db = require('../config/database');

const router = express.Router();

// 获取所有配送区域
router.get('/', async (req, res) => {
  try {
    const { warehouseId } = req.query;
    
    let sql = `
      SELECT r.*, w.name as warehouse_name, u.name as default_delivery_name
      FROM delivery_regions r
      LEFT JOIN warehouses w ON r.warehouse_id = w.id
      LEFT JOIN users u ON r.default_delivery_id = u.id
      WHERE r.status = 1
    `;
    const params = [];

    if (warehouseId) {
      sql += ' AND r.warehouse_id = ?';
      params.push(warehouseId);
    }

    sql += ' ORDER BY r.created_at DESC';

    const [regions] = await db.query(sql, params);

    res.json({
      success: true,
      data: regions
    });

  } catch (error) {
    console.error('获取区域列表错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 创建配送区域
router.post('/', [
  body('warehouseId').isInt().withMessage('请选择仓库'),
  body('name').notEmpty().withMessage('区域名称不能为空')
], async (req, res) => {
  try {
    const { warehouseId, name, boundary, defaultDeliveryId } = req.body;

    const [result] = await db.query(
      'INSERT INTO delivery_regions (warehouse_id, name, boundary, default_delivery_id) VALUES (?, ?, ?, ?)',
      [warehouseId, name, boundary ? JSON.stringify(boundary) : null, defaultDeliveryId || null]
    );

    res.status(201).json({
      success: true,
      message: '区域创建成功',
      data: { regionId: result.insertId }
    });

  } catch (error) {
    console.error('创建区域错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 更新区域
router.put('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const { name, boundary, defaultDeliveryId } = req.body;

    const [result] = await db.query(
      'UPDATE delivery_regions SET name = ?, boundary = ?, default_delivery_id = ? WHERE id = ?',
      [name, boundary ? JSON.stringify(boundary) : null, defaultDeliveryId, id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: '区域不存在' });
    }

    res.json({
      success: true,
      message: '区域更新成功'
    });

  } catch (error) {
    console.error('更新区域错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 删除区域
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const [result] = await db.query(
      'UPDATE delivery_regions SET status = 0 WHERE id = ?',
      [id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: '区域不存在' });
    }

    res.json({
      success: true,
      message: '区域已删除'
    });

  } catch (error) {
    console.error('删除区域错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 根据地址坐标匹配配送区域
router.post('/match', async (req, res) => {
  try {
    const { lat, lng } = req.body;

    // 这里简化处理：返回距离最近的仓库
    // 实际应该用点是否在多边形内的算法
    const [warehouses] = await db.query(
      `SELECT *, 
        (6371 * acos(cos(radians(?)) * cos(radians(lat)) * cos(radians(lng) - radians(?)) + sin(radians(?)) * sin(radians(lat)))) AS distance
      FROM warehouses
      WHERE status = 1
      ORDER BY distance
      LIMIT 1`,
      [lat, lng, lat]
    );

    if (warehouses.length === 0) {
      return res.status(404).json({ error: '未找到匹配的仓库' });
    }

    const warehouse = warehouses[0];

    // 获取该仓库的配送区域
    const [regions] = await db.query(
      'SELECT * FROM delivery_regions WHERE warehouse_id = ? AND status = 1',
      [warehouse.id]
    );

    res.json({
      success: true,
      data: {
        warehouse,
        regions,
        matchedRegion: regions[0] || null // 简化：返回第一个区域
      }
    });

  } catch (error) {
    console.error('匹配区域错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

module.exports = router;