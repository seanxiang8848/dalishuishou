# 大力水手 - 签名配置指南

## 🔐 创建签名密钥库

### 1. 生成密钥库文件

在终端执行以下命令：

```bash
cd /root/.openclaw/workspace/大力水手/android

keytool -genkey -v \
  -keystore dalishuishou.keystore \
  -alias dalishuishou \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### 2. 填写信息

按提示输入：
- 密钥库密码：`Dalishuishou2026!`（建议设置强密码）
- 密钥密码：可以与密钥库密码相同
- 姓名：`大力水手`
- 组织单位：`配送团队`
- 组织：`大力水手科技`
- 城市：`成都`
- 省份：`四川`
- 国家代码：`CN`

### 3. 配置签名文件

创建 `signing.properties` 文件：

```bash
cd /root/.openclaw/workspace/大力水手/android
cp signing.properties.example signing.properties
```

编辑 `signing.properties`：

```properties
RELEASE_STORE_FILE=dalishuishou.keystore
RELEASE_STORE_PASSWORD=你的密钥库密码
RELEASE_KEY_ALIAS=dalishuishou
RELEASE_KEY_PASSWORD=你的密钥密码
```

## 📦 构建Release包

### 1. 构建APK

```bash
./gradlew assembleRelease
```

输出位置：
```
app/build/outputs/apk/release/app-release.apk
```

### 2. 构建AAB（Google Play用）

```bash
./gradlew bundleRelease
```

输出位置：
```
app/build/outputs/bundle/release/app-release.aab
```

## 🔒 安全提示

⚠️ **重要**：
- `dalishuishou.keystore` 文件必须妥善保管
- `signing.properties` 不要提交到Git仓库（已加入.gitignore）
- 密钥丢失将无法更新应用

## 📋 多渠道打包

已配置渠道：
- `official`：官方渠道
- `douyin`：抖音渠道

构建指定渠道：
```bash
./gradlew assembleOfficialRelease
./gradlew assembleDouyinRelease
```
