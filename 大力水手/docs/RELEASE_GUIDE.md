# 大力水手 - 上线发布指南

## 🚀 上线步骤

### 步骤1: 创建签名文件（在本地电脑执行）

#### Windows:
1. 打开CMD或PowerShell
2. 进入项目目录:
```cmd
cd C:\path\to\大力水手\android
```

3. 运行脚本:
```cmd
create_keystore.bat
```

#### Mac/Linux:
```bash
cd /path/to/大力水手/android
chmod +x create_keystore.sh
./create_keystore.sh
```

#### 手动创建（如果脚本无法运行）:
```bash
keytool -genkey -v \
  -keystore dalishuishou.keystore \
  -alias dalishuishou \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

按提示输入:
- 密钥库密码: `Dalishuishou2026!`
- 密钥密码: `Dalishuishou2026!` (回车与上面相同)
- 姓名: `大力水手`
- 组织单位: `配送团队`
- 组织: `大力水手科技`
- 城市: `成都`
- 省份: `四川`
- 国家: `CN`

### 步骤2: 获取SHA1并更新高德地图Key

执行:
```bash
keytool -list -v -keystore dalishuishou.keystore -alias dalishuishou
```

复制 **SHA1** 值，到高德地图控制台更新应用的SHA1。

### 步骤3: 构建Release版本

在项目目录执行:

```bash
# 进入android目录
cd android

# 构建Release APK
./gradlew assembleRelease

# 或构建多渠道包
./gradlew assembleOfficialRelease
./gradlew assembleDouyinRelease

# 或构建AAB (Google Play)
./gradlew bundleRelease
```

输出文件位置:
- APK: `app/build/outputs/apk/release/`
- AAB: `app/build/outputs/bundle/release/`

### 步骤4: 验证APK

```bash
# 检查APK签名
jarsigner -verify -verbose -certs app-release.apk

# 查看APK信息
aapt dump badging app-release.apk
```

### 步骤5: 上传到应用商店

#### 华为应用市场:
1. 访问 https://developer.huawei.com/
2. 注册开发者账号
3. 创建应用，上传APK
4. 填写应用信息
5. 提交审核

#### 小米应用商店:
1. 访问 http://dev.mi.com/
2. 注册开发者账号
3. 上传APK
4. 提交审核

#### 腾讯应用宝:
1. 访问 https://open.qq.com/
2. 注册开发者账号
3. 上传APK
4. 提交审核

#### 其他渠道:
- OPPO应用商店: https://open.oppomobile.com/
- vivo应用商店: https://dev.vivo.com.cn/
- 百度手机助手: http://app.baidu.com/

---

## 📋 上线前检查清单

### 功能检查
- [ ] 登录/注册正常
- [ ] 订单列表加载正常
- [ ] 订单详情显示正常
- [ ] 派单/取消功能正常
- [ ] 扫码功能正常
- [ ] 地图显示正常
- [ ] 推送接收正常
- [ ] 下拉刷新/上拉加载正常

### 性能检查
- [ ] 冷启动时间 < 3秒
- [ ] 列表滑动流畅
- [ ] 图片加载正常
- [ ] 内存占用合理

### 安全检查
- [ ] 签名文件已创建
- [ ] 混淆规则已配置
- [ ] 调试日志已关闭
- [ ] 测试账号已移除

### 资料准备
- [ ] 应用图标 (512x512)
- [ ] 应用截图 (5张)
- [ ] 应用描述
- [ ] 隐私政策链接
- [ ] 用户协议链接

---

## ⚠️ 重要提醒

### 签名文件保管
- **keystore文件是应用的身份证明**
- **丢失后无法更新应用，必须重新发布**
- **建议备份到多个安全位置**
  - 本地电脑
  - 云盘
  - U盘

### 密码管理
- 密钥密码: `Dalishuishou2026!`
- **请勿泄露给他人**
- **请勿提交到Git仓库**

---

## 🔧 常见问题

### Q: 构建失败怎么办?
A: 检查:
1. 签名文件是否存在
2. signing.properties配置是否正确
3. 网络连接是否正常

### Q: 如何更新已发布的应用?
A:
1. 使用相同的keystore文件
2. 提高versionCode
3. 重新构建Release
4. 上传到应用商店

### Q: 如何支持64位?
A: build.gradle中已配置，默认支持

---

## 📞 支持

如有问题，随时联系！

**祝上线顺利!** 🎉
