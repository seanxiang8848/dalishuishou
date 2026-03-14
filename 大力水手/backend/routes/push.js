const express = require('express');
const axios = require('axios');
const crypto = require('crypto');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// 极光推送配置
const JPUSH_CONFIG = {
  appKey: '10071eebb63f3dbd0b32e965',
  masterSecret: process.env.JPUSH_MASTER_SECRET || '', // 需要从环境变量获取
  apiUrl: 'https://api.jpush.cn/v3/push'
};

/**
 * 生成极光推送认证Header
 */
function generateAuthHeader() {
  const credentials = Buffer.from(`${JPUSH_CONFIG.appKey}:${JPUSH_CONFIG.masterSecret}`).toString('base64');
  return `Basic ${credentials}`;
}

/**
 * 发送推送通知
 * POST /push/send
 */
router.post('/send', authenticateToken, async (req, res) => {
  try {
    const { 
      type = 'notification',  // notification: 通知, message: 透传消息
      targetType,             // alias: 别名, tag: 标签, all: 广播
      target,                 // 目标值（用户ID或仓库ID）
      title,                  // 标题
      content,                // 内容
      extras = {}             // 附加数据
    } = req.body;

    // 验证参数
    if (!targetType || !title || !content) {
      return res.status(400).json({
        success: false,
        message: '缺少必要参数：targetType, title, content'
      });
    }

    // 构建推送目标
    let audience = {};
    switch (targetType) {
      case 'alias':
        // 发送给指定用户 alias格式: user_${userId}
        audience = { alias: [`user_${target}`] };
        break;
      case 'tag':
        // 发送给指定仓库 tag格式: warehouse_${warehouseId}
        audience = { tag: [`warehouse_${target}`] };
        break;
      case 'all':
        // 广播给所有用户
        audience = 'all';
        break;
      default:
        return res.status(400).json({
          success: false,
          message: '无效的targetType，可选: alias, tag, all'
        });
    }

    // 构建推送内容
    const pushData = {
      platform: 'android',
      audience: audience,
      notification: type === 'notification' ? {
        alert: content,
        android: {
          title: title,
          extras: extras
        }
      } : undefined,
      message: type === 'message' ? {
        msg_content: content,
        title: title,
        extras: extras
      } : undefined,
      options: {
        time_to_live: 86400,  // 24小时有效期
        apns_production: false // 开发环境
      }
    };

    // 如果没有配置masterSecret，返回模拟成功
    if (!JPUSH_CONFIG.masterSecret) {
      console.log('[Push Mock] 推送内容:', JSON.stringify(pushData, null, 2));
      
      // 记录到数据库
      await db.execute(
        `INSERT INTO push_logs (target_type, target, title, content, extras, status, created_at) 
         VALUES (?, ?, ?, ?, ?, ?, NOW())`,
        [targetType, target?.toString() || 'all', title, content, JSON.stringify(extras), 'mock']
      );
      
      return res.json({
        success: true,
        message: '推送已记录（模拟模式，未配置MasterSecret）',
        data: {
          pushId: `mock_${Date.now()}`,
          target: targetType === 'all' ? 'all' : `${targetType}_${target}`
        }
      });
    }

    // 调用极光推送API
    const response = await axios.post(JPUSH_CONFIG.apiUrl, pushData, {
      headers: {
        'Authorization': generateAuthHeader(),
        'Content-Type': 'application/json'
      }
    });

    // 记录到数据库
    await db.execute(
      `INSERT INTO push_logs (target_type, target, title, content, extras, status, push_id, created_at) 
       VALUES (?, ?, ?, ?, ?, ?, ?, NOW())`,
      [targetType, target?.toString() || 'all', title, content, JSON.stringify(extras), 'sent', response.data.sendno]
    );

    res.json({
      success: true,
      message: '推送发送成功',
      data: {
        pushId: response.data.sendno,
        target: targetType === 'all' ? 'all' : `${targetType}_${target}`
      }
    });

  } catch (error) {
    console.error('发送推送失败:', error);
    res.status(500).json({
      success: false,
      message: '发送推送失败',
      error: error.message
    });
  }
});

/**
 * 发送新订单推送
 * POST /push/new-order
 * 给配送员发送新订单通知
 */
router.post('/new-order', authenticateToken, async (req, res) => {
  try {
    const { deliveryId, orderId, orderNo, customerName, address } = req.body;

    if (!deliveryId || !orderId) {
      return res.status(400).json({
        success: false,
        message: '缺少必要参数：deliveryId, orderId'
      });
    }

    const title = '新订单派单';
    const content = `您有新订单待配送，客户：${customerName || '未知'}`;
    
    const extras = {
      type: 'new_order',
      orderId: orderId,
      orderNo: orderNo || '',
      customerName: customerName || '',
      address: address || ''
    };

    // 转发到send接口
    req.body = {
      type: 'notification',
      targetType: 'alias',
      target: deliveryId,
      title,
      content,
      extras
    };

    // 调用send逻辑
    router.handle(req, res, () => {});

  } catch (error) {
    console.error('发送新订单推送失败:', error);
    res.status(500).json({
      success: false,
      message: '发送推送失败'
    });
  }
});

/**
 * 发送订单分配推送
 * POST /push/order-assigned
 */
router.post('/order-assigned', authenticateToken, async (req, res) => {
  try {
    const { deliveryId, orderId, orderNo, warehouseName } = req.body;

    const title = '订单已分配';
    const content = `${warehouseName || '仓库'}给您分配了新订单`;
    
    const extras = {
      type: 'order_assigned',
      orderId: orderId,
      orderNo: orderNo || ''
    };

    req.body = {
      type: 'notification',
      targetType: 'alias',
      target: deliveryId,
      title,
      content,
      extras
    };

    router.handle(req, res, () => {});

  } catch (error) {
    console.error('发送订单分配推送失败:', error);
    res.status(500).json({
      success: false,
      message: '发送推送失败'
    });
  }
});

/**
 * 发送订单取消推送
 * POST /push/order-cancelled
 */
router.post('/order-cancelled', authenticateToken, async (req, res) => {
  try {
    const { deliveryId, orderId, orderNo, reason } = req.body;

    const title = '订单已取消';
    const content = `订单${orderNo || ''}已被取消${reason ? '，原因：' + reason : ''}`;
    
    const extras = {
      type: 'order_cancelled',
      orderId: orderId,
      orderNo: orderNo || ''
    };

    req.body = {
      type: 'notification',
      targetType: 'alias',
      target: deliveryId,
      title,
      content,
      extras
    };

    router.handle(req, res, () => {});

  } catch (error) {
    console.error('发送订单取消推送失败:', error);
    res.status(500).json({
      success: false,
      message: '发送推送失败'
    });
  }
});

/**
 * 广播系统通知
 * POST /push/broadcast
 * 给所有用户发送系统通知
 */
router.post('/broadcast', authenticateToken, async (req, res) => {
  try {
    const { title, content, extras = {} } = req.body;

    if (!title || !content) {
      return res.status(400).json({
        success: false,
        message: '缺少必要参数：title, content'
      });
    }

    req.body = {
      type: 'notification',
      targetType: 'all',
      title,
      content,
      extras: {
        type: 'system',
        ...extras
      }
    };

    router.handle(req, res, () => {});

  } catch (error) {
    console.error('广播推送失败:', error);
    res.status(500).json({
      success: false,
      message: '广播推送失败'
    });
  }
});

/**
 * 获取推送记录
 * GET /push/logs
 */
router.get('/logs', authenticateToken, async (req, res) => {
  try {
    const { page = 1, limit = 20, targetType, status } = req.query;
    const offset = (parseInt(page) - 1) * parseInt(limit);

    let whereClause = 'WHERE 1=1';
    const params = [];

    if (targetType) {
      whereClause += ' AND target_type = ?';
      params.push(targetType);
    }
    if (status) {
      whereClause += ' AND status = ?';
      params.push(status);
    }

    // 获取总数
    const [countResult] = await db.execute(
      `SELECT COUNT(*) as total FROM push_logs ${whereClause}`,
      params
    );
    const total = countResult[0].total;

    // 获取记录
    const [logs] = await db.execute(
      `SELECT * FROM push_logs ${whereClause} 
       ORDER BY created_at DESC LIMIT ? OFFSET ?`,
      [...params, parseInt(limit), offset]
    );

    res.json({
      success: true,
      data: {
        list: logs,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / parseInt(limit))
        }
      }
    });

  } catch (error) {
    console.error('获取推送记录失败:', error);
    res.status(500).json({
      success: false,
      message: '获取推送记录失败'
    });
  }
});

module.exports = router;
