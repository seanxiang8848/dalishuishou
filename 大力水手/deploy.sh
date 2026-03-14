#!/bin/bash
# 大力水手 - 部署脚本
# 用法: ./deploy.sh

set -e

echo "🚀 开始部署大力水手..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查命令是否存在
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# 安装依赖
echo -e "${YELLOW}📦 检查并安装依赖...${NC}"

if ! command_exists node; then
    echo -e "${RED}❌ Node.js 未安装${NC}"
    exit 1
fi

if ! command_exists mysql; then
    echo -e "${RED}❌ MySQL 未安装${NC}"
    echo "请运行: apt-get install mysql-server"
    exit 1
fi

# 进入后端目录
cd /root/.openclaw/workspace/大力水手/backend

# 安装npm依赖
echo -e "${YELLOW}📦 安装 npm 依赖...${NC}"
npm install

# 创建环境文件
if [ ! -f .env ]; then
    echo -e "${YELLOW}📝 创建 .env 文件...${NC}"
    cp .env.example .env
    echo -e "${YELLOW}⚠️  请编辑 .env 文件，设置正确的数据库密码和API密钥${NC}"
fi

# 初始化数据库
echo -e "${YELLOW}🗄️  初始化数据库...${NC}"
node scripts/init-db.js

# 创建上传目录
mkdir -p uploads

# 启动服务
echo -e "${GREEN}✅ 部署完成！${NC}"
echo ""
echo -e "${GREEN}启动服务:${NC}"
echo "  开发模式: npm run dev"
echo "  生产模式: npm start"
echo ""
echo -e "${GREEN}访问地址:${NC}"
echo "  API地址: http://$(hostname -I | awk '{print $1}'):3000"
echo "  健康检查: http://$(hostname -I | awk '{print $1}'):3000/health"
