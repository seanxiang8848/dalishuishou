const mysql = require('mysql2/promise');
require('dotenv').config();

const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: process.env.DB_PORT || 3306,
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  multipleStatements: true
};

const databaseName = process.env.DB_NAME || 'dalishuishou';

// 建表SQL
const createTablesSQL = `
-- 用户表
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  role ENUM('admin','warehouse_manager','delivery') NOT NULL COMMENT '角色',
  warehouse_id INT DEFAULT NULL COMMENT '所属仓库ID',
  avatar VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  status TINYINT DEFAULT 1 COMMENT '状态:0禁用 1启用',
  last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_phone (phone),
  INDEX idx_role (role),
  INDEX idx_warehouse (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouses (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '仓库名称',
  address VARCHAR(255) NOT NULL COMMENT '地址',
  lat DECIMAL(10,8) NOT NULL COMMENT '纬度',
  lng DECIMAL(11,8) NOT NULL COMMENT '经度',
  manager_id INT DEFAULT NULL COMMENT '负责人ID',
  status TINYINT DEFAULT 1 COMMENT '状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_manager (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 配送区域表
CREATE TABLE IF NOT EXISTS delivery_regions (
  id INT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id INT NOT NULL COMMENT '所属仓库ID',
  name VARCHAR(100) NOT NULL COMMENT '区域名称',
  boundary TEXT COMMENT '区域边界坐标JSON',
  default_delivery_id INT DEFAULT NULL COMMENT '默认配送员ID',
  status TINYINT DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
  FOREIGN KEY (default_delivery_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_warehouse (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送区域表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '订单编号',
  platform ENUM('douyin','manual') DEFAULT 'manual' COMMENT '来源平台',
  platform_order_id VARCHAR(100) DEFAULT NULL COMMENT '平台订单ID',
  customer_name VARCHAR(50) NOT NULL COMMENT '客户姓名',
  customer_phone VARCHAR(20) NOT NULL COMMENT '客户电话',
  address VARCHAR(255) NOT NULL COMMENT '配送地址',
  lat DECIMAL(10,8) DEFAULT NULL COMMENT '地址纬度',
  lng DECIMAL(11,8) DEFAULT NULL COMMENT '地址经度',
  product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
  quantity INT DEFAULT 1 COMMENT '数量',
  remark TEXT COMMENT '备注',
  status ENUM('pending','assigned','delivering','completed','cancelled') DEFAULT 'pending' COMMENT '状态',
  warehouse_id INT DEFAULT NULL COMMENT '分配仓库ID',
  delivery_id INT DEFAULT NULL COMMENT '配送员ID',
  assigned_at TIMESTAMP NULL COMMENT '分配时间',
  picked_up_at TIMESTAMP NULL COMMENT '取货时间',
  delivered_at TIMESTAMP NULL COMMENT '送达时间',
  delivery_photo VARCHAR(500) DEFAULT NULL COMMENT '配送照片',
  delivery_note TEXT COMMENT '配送备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE SET NULL,
  FOREIGN KEY (delivery_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_status (status),
  INDEX idx_warehouse (warehouse_id),
  INDEX idx_delivery (delivery_id),
  INDEX idx_platform_order (platform_order_id),
  INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 配送记录表
CREATE TABLE IF NOT EXISTS delivery_logs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL COMMENT '订单ID',
  delivery_id INT NOT NULL COMMENT '配送员ID',
  action ENUM('accept','pickup','deliver','cancel','location') NOT NULL COMMENT '操作类型',
  location_lat DECIMAL(10,8) DEFAULT NULL COMMENT '位置纬度',
  location_lng DECIMAL(11,8) DEFAULT NULL COMMENT '位置经度',
  photo_url VARCHAR(500) DEFAULT NULL COMMENT '照片URL',
  note TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  FOREIGN KEY (delivery_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_order (order_id),
  INDEX idx_delivery (delivery_id),
  INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送记录表';

-- 插入默认管理员账号 (密码: admin123)
INSERT IGNORE INTO users (phone, password_hash, name, role, status) VALUES
('13800138000', '$2a$10$YourHashedPasswordHere', '系统管理员', 'admin', 1);
`;

async function initDatabase() {
  try {
    // 连接MySQL（不指定数据库）
    const connection = await mysql.createConnection(dbConfig);
    
    // 创建数据库
    await connection.execute(`CREATE DATABASE IF NOT EXISTS ${databaseName} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci`);
    console.log(`✅ 数据库 ${databaseName} 创建成功`);
    
    // 使用数据库
    await connection.query(`USE ${databaseName}`);
    
    // 创建表
    await connection.query(createTablesSQL);
    console.log('✅ 数据表创建成功');
    
    await connection.end();
    console.log('🎉 数据库初始化完成');
    
  } catch (error) {
    console.error('❌ 数据库初始化失败:', error.message);
    process.exit(1);
  }
}

// 如果直接运行此脚本
if (require.main === module) {
  initDatabase();
}

module.exports = { initDatabase };