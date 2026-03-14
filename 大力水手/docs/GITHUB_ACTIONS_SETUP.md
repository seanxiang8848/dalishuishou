# 大力水手 - GitHub Actions 自动构建配置指南

## 🚀 配置步骤

### 步骤1: 创建 GitHub 仓库

1. 访问 https://github.com/new
2. 仓库名称: `dalishuishou` (或其他名称)
3. 选择 **Private** (私有仓库，保护代码)
4. 点击 **Create repository**

---

### 步骤2: 上传代码到 GitHub

在本地项目文件夹执行：

```bash
cd /path/to/大力水手

# 初始化git
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit"

# 连接到GitHub仓库
git remote add origin https://github.com/你的用户名/dalishuishou.git

# 推送代码
git push -u origin main
```

---

### 步骤3: 配置密钥 (重要！)

需要将签名密钥配置到 GitHub Secrets 中。

#### 3.1 将 keystore 转为 base64

在服务器上执行：
```bash
cd ~/.openclaw/workspace/大力水手/android
base64 dalishuishou.keystore
```

复制输出的长字符串。

#### 3.2 在 GitHub 配置 Secrets

1. 打开 GitHub 仓库页面
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**
4. 添加以下4个密钥：

| Secret名称 | 值 |
|-----------|-----|
| `KEYSTORE_BASE64` | 上面复制的base64字符串 |
| `KEYSTORE_PASSWORD` | Dalishuishou2026! |
| `KEY_ALIAS` | dalishuishou |
| `KEY_PASSWORD` | Dalishuishou2026! |

---

### 步骤4: 触发自动构建

配置完成后，每次推送代码会自动构建：

```bash
# 修改任意文件，比如更新版本号
git add .
git commit -m "Update version"
git push
```

---

## 📦 获取构建好的APK

### 方法1: GitHub Actions 页面下载

1. 打开 GitHub 仓库
2. 点击 **Actions** 标签
3. 选择最新的 workflow 运行
4. 页面底部有 **Artifacts** 部分
5. 点击 **app-release** 下载APK

### 方法2: 打标签自动发布 Release

```bash
# 创建版本标签
git tag -a v1.0.0 -m "Release v1.0.0"

# 推送标签
git push origin v1.0.0
```

推送标签后，GitHub 会自动创建 Release，APK会附在 Release 中。

---

## 🔧 常见问题

### Q: 构建失败怎么办？
A: 
1. 点击 Actions 查看详细日志
2. 检查 Secrets 是否配置正确
3. 检查代码是否有语法错误

### Q: 如何更新密钥？
A: 在 Settings → Secrets 中删除旧的，重新添加新的。

### Q: 构建时间多久？
A: 通常 5-10 分钟。

---

## 📋 配置检查清单

- [ ] 创建 GitHub 仓库
- [ ] 上传代码
- [ ] 配置 KEYSTORE_BASE64
- [ ] 配置 KEYSTORE_PASSWORD
- [ ] 配置 KEY_ALIAS
- [ ] 配置 KEY_PASSWORD
- [ ] 触发一次构建测试
- [ ] 下载APK验证

---

**配置完成后，每次推送代码自动构建APK！** 🎉
