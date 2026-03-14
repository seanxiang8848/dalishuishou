# 🥬 大力水手 (Popeye)

> 专业送水配送管理系统

[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Node.js](https://img.shields.io/badge/Node.js-339933?style=flat&logo=nodedotjs&logoColor=white)](https://nodejs.org/)

## 📱 项目简介

大力水手是一款专为送水配送行业打造的移动管理应用，帮助配送站实现订单管理、配送调度、扫码核销、数据统计等全流程数字化管理。

## ✨ 核心功能

### 用户系统
- ✅ 用户注册/登录
- ✅ 个人信息管理
- ✅ 密码修改

### 订单管理
- ✅ 订单列表（支持下拉刷新/上拉加载）
- ✅ 订单详情查看
- ✅ 订单派单/取消
- ✅ 多状态筛选（待派单/配送中/已完成）

### 扫码核销
- ✅ ZXing 二维码扫描
- ✅ 条形码识别
- ✅ 手动输入订单号

### 数据统计
- ✅ 首页实时数据看板
- ✅ 个人业绩统计
- ✅ 配送员工作量统计

### 地图导航
- ✅ 高德地图集成
- ✅ 配送路线规划
- ✅ 多途经点显示

### 消息推送
- ✅ 极光推送集成
- ✅ 新订单通知
- ✅ 订单分配通知
- ✅ 订单取消通知

## 🏗️ 技术架构

```
大力水手/
├── android/          # Android 客户端 (Kotlin + Jetpack Compose)
├── backend/          # 后端 API (Node.js + Express + MySQL)
├── database/         # 数据库脚本
└── docs/             # 项目文档
```

### 前端技术栈
- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **网络**: Retrofit + OkHttp
- **图片加载**: Coil
- **扫码**: ZXing
- **推送**: 极光推送
- **地图**: 高德地图 SDK

### 后端技术栈
- **运行时**: Node.js
- **框架**: Express
- **数据库**: MySQL
- **实时通信**: Socket.io

## 🚀 快速开始

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Node.js 16+
- MySQL 8.0+

### 后端部署

```bash
cd backend
npm install

# 配置数据库
cp .env.example .env
# 编辑 .env 配置数据库连接

# 初始化数据库
node scripts/init-db.js

# 启动服务
npm start
```

### Android 构建

```bash
cd android

# 调试版本
./gradlew assembleDebug

# 发布版本（需要配置签名）
./gradlew assembleRelease
```

## 📚 文档

- [API 文档](./docs/API.md)
- [签名配置指南](./docs/SIGNING_GUIDE.md)
- [发布检查清单](./docs/RELEASE_CHECKLIST.md)
- [项目进度](./PROGRESS.md)

## 📄 许可证

MIT License

---

**让配送像吃菠菜一样给力！** 💪
