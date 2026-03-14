const express = require('express');
const { body, validationResult, query } = require('express-validator');
const db = require('../config/database');

const router = express.Router();

// 获取订单列表（支持分页和筛选）
router.get('/', async (req, res) => {
  try {
    const { 
      page = 1, 
      limit = 20, 
      status, 
      warehouseId, 
      deliveryId,
      keyword,
      startDate,
      endDate
    } = req.query;

    const offset = (parseInt(page) - 1) * parseInt(limit);
    
    // 构建查询条件
    let whereClause = 'WHERE 1=1';
    const params = [];

    if (status) {
      whereClause += ' AND o.status = ?';
      params.push(status);
    }
    if (warehouseId) {
      whereClause += ' AND o.warehouse_id = ?';
      params.push(warehouseId);
    }
    if (deliveryId) {
      whereClause += ' AND o.delivery_id = ?';
      params.push(deliveryId);
    }
    if (keyword) {
      whereClause += ' AND (o.order_no LIKE ? OR o.customer_name LIKE ? OR o.customer_phone LIKE ?)';
      const likeKeyword = `%${keyword}%`;
      params.push(likeKeyword, likeKeyword, likeKeyword);
    }
    if (startDate) {
      whereClause += ' AND o.created_at >= ?';
      params.push(startDate);
    }
    if (endDate) {
      whereClause += ' AND o.created_at <= ?';
      params.push(endDate);
    }

    // 查询总数
    const [countResult] = await db.query(
      `SELECT COUNT(*) as total FROM orders o ${whereClause}`,
      params
    );
    const total = countResult[0].total;

    // 查询列表
    const [orders] = await db.query(
      `SELECT o.*, 
        w.name as warehouse_name,
        d.name as delivery_name
      FROM orders o
      LEFT JOIN warehouses w ON o.warehouse_id = w.id
      LEFT JOIN users d ON o.delivery_id = d.id
      ${whereClause}
      ORDER BY o.created_at DESC
      LIMIT ? OFFSET ?`,
      [...params, parseInt(limit), offset]
    );

    res.json({
      success: true,
      data: {
        list: orders,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / parseInt(limit))
        }
      }
    });

  } catch (error) {
    console.error('获取订单列表错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 获取订单详情
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const [orders] = await db.query(
      `SELECT o.*, 
        w.name as warehouse_name, w.address as warehouse_address, w.lat as warehouse_lat, w.lng as warehouse_lng,
        d.name as delivery_name, d.phone as delivery_phone
      FROM orders o
      LEFT JOIN warehouses w ON o.warehouse_id = w.id
      LEFT JOIN users d ON o.delivery_id = d.id
      WHERE o.id = ?`,
      [id]
    );

    if (orders.length === 0) {
      return res.status(404).json({ error: '订单不存在' });
    }

    // 获取配送记录
    const [logs] = await db.query(
      `SELECT l.*, d.name as delivery_name
      FROM delivery_logs l
      JOIN users d ON l.delivery_id = d.id
      WHERE l.order_id = ?
      ORDER BY l.created_at DESC`,
      [id]
    );

    res.json({
      success: true,
      data: {
        ...orders[0],
        logs
      }
    });

  } catch (error) {
    console.error('获取订单详情错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 创建订单（手动录入）
router.post('/', [
  body('customerName').notEmpty().withMessage('客户姓名不能为空'),
  body('customerPhone').isMobilePhone('zh-CN').withMessage('请输入正确的手机号'),
  body('address').notEmpty().withMessage('地址不能为空'),
  body('productName').notEmpty().withMessage('商品名称不能为空'),
  body('quantity').isInt({ min: 1 }).withMessage('数量至少为1')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ error: errors.array()[0].msg });
    }

    const {
      customerName,
      customerPhone,
      address,
      lat,
      lng,
      productName,
      quantity,
      remark,
      warehouseId
    } = req.body;

    // 生成订单号：SDD + 年月日 + 6位随机数
    const dateStr = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    const randomStr = Math.floor(100000 + Math.random() * 900000);
    const orderNo = `SDD${dateStr}${randomStr}`;

    const [result] = await db.query(
      `INSERT INTO orders 
      (order_no, platform, customer_name, customer_phone, address, lat, lng, 
       product_name, quantity, remark, warehouse_id, status) 
      VALUES (?, 'manual', ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending')`,
      [orderNo, customerName, customerPhone, address, lat, lng, productName, quantity, remark, warehouseId]
    );

    res.status(201).json({
      success: true,
      message: '订单创建成功',
      data: { orderId: result.insertId, orderNo }
    });

  } catch (error) {
    console.error('创建订单错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 分配订单给配送员
router.post('/:id/assign', [
  body('deliveryId').isInt().withMessage('请选择配送员'),
  body('warehouseId').isInt().withMessage('请选择仓库')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ error: errors.array()[0].msg });
    }

    const { id } = req.params;
    const { deliveryId, warehouseId } = req.body;

    // 检查订单状态
    const [orders] = await db.query('SELECT status FROM orders WHERE id = ?', [id]);
    if (orders.length === 0) {
      return res.status(404).json({ error: '订单不存在' });
    }
    if (orders[0].status !== 'pending') {
      return res.status(400).json({ error: '订单状态不允许分配' });
    }

    // 更新订单
    await db.query(
      'UPDATE orders SET delivery_id = ?, warehouse_id = ?, status = "assigned", assigned_at = NOW() WHERE id = ?',
      [deliveryId, warehouseId, id]
    );

    // 记录配送日志
    await db.query(
      'INSERT INTO delivery_logs (order_id, delivery_id, action) VALUES (?, ?, "accept")',
      [id, deliveryId]
    );

    res.json({
      success: true,
      message: '订单分配成功'
    });

  } catch (error) {
    console.error('分配订单错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 更新订单状态（配送员操作）
router.post('/:id/status', [
  body('status').isIn(['pickup', 'deliver', 'cancel']).withMessage('状态类型错误'),
  body('locationLat').optional().isFloat(),
  body('locationLng').optional().isFloat(),
  body('note').optional().isString()
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ error: errors.array()[0].msg });
    }

    const { id } = req.params;
    const { status, locationLat, locationLng, note, photoUrl } = req.body;
    // const deliveryId = req.user.userId; // 从JWT获取
    const deliveryId = 1; // 临时使用

    const [orders] = await db.query('SELECT * FROM orders WHERE id = ?', [id]);
    if (orders.length === 0) {
      return res.status(404).json({ error: '订单不存在' });
    }

    const order = orders[0];

    // 验证配送员权限
    if (order.delivery_id !== deliveryId) {
      return res.status(403).json({ error: '无权操作此订单' });
    }

    // 状态流转检查
    const statusFlow = {
      'pickup': { from: 'assigned', to: 'delivering', field: 'picked_up_at' },
      'deliver': { from: 'delivering', to: 'completed', field: 'delivered_at' },
      'cancel': { from: ['assigned', 'delivering'], to: 'cancelled', field: null }
    };

    const flow = statusFlow[status];
    const allowedFrom = Array.isArray(flow.from) ? flow.from : [flow.from];
    
    if (!allowedFrom.includes(order.status)) {
      return res.status(400).json({ error: '当前状态不允许此操作' });
    }

    // 更新订单状态
    let updateSql = 'UPDATE orders SET status = ?';
    let params = [flow.to];

    if (flow.field) {
      updateSql += `, ${flow.field} = NOW()`;
    }
    if (status === 'deliver') {
      updateSql += ', delivery_photo = ?, delivery_note = ?';
      params.push(photoUrl || null, note || null);
    }

    updateSql += ' WHERE id = ?';
    params.push(id);

    await db.query(updateSql, params);

    // 记录配送日志
    await db.query(
      `INSERT INTO delivery_logs (order_id, delivery_id, action, location_lat, location_lng, note, photo_url) 
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [id, deliveryId, status, locationLat, locationLng, note, photoUrl]
    );

    res.json({
      success: true,
      message: '状态更新成功'
    });

  } catch (error) {
    console.error('更新订单状态错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 获取配送员当前订单
router.get('/delivery/current', async (req, res) => {
  try {
    // const deliveryId = req.user.userId;
    const deliveryId = req.query.deliveryId || 1;

    const [orders] = await db.query(
      `SELECT o.*, w.name as warehouse_name, w.address as warehouse_address
      FROM orders o
      LEFT JOIN warehouses w ON o.warehouse_id = w.id
      WHERE o.delivery_id = ? AND o.status IN ('assigned', 'delivering')
      ORDER BY o.assigned_at DESC`,
      [deliveryId]
    );

    res.json({
      success: true,
      data: orders
    });

  } catch (error) {
    console.error('获取当前订单错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

// 批量分配订单
router.post('/batch-assign', [
  body('orderIds').isArray({ min: 1 }).withMessage('请选择至少一个订单'),
  body('deliveryId').isInt().withMessage('请选择配送员')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ error: errors.array()[0].msg });
    }

    const { orderIds, deliveryId, warehouseId } = req.body;

    const connection = await db.getConnection();
    await connection.beginTransaction();

    try {
      for (const orderId of orderIds) {
        await connection.query(
          'UPDATE orders SET delivery_id = ?, warehouse_id = ?, status = "assigned", assigned_at = NOW() WHERE id = ? AND status = "pending"',
          [deliveryId, warehouseId, orderId]
        );
        await connection.query(
          'INSERT INTO delivery_logs (order_id, delivery_id, action) VALUES (?, ?, "accept")',
          [orderId, deliveryId]
        );
      }

      await connection.commit();
      res.json({ success: true, message: '批量分配成功' });

    } catch (err) {
      await connection.rollback();
      throw err;
    } finally {
      connection.release();
    }

  } catch (error) {
    console.error('批量分配错误:', error);
    res.status(500).json({ error: '服务器错误' });
  }
});

module.exports = router;