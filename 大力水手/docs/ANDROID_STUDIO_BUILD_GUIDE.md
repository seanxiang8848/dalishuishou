# 大力水手 - Android Studio 构建详细步骤

## 准备工作

### 1. 下载项目文件
把服务器上的项目文件下载到您的电脑：
```
/root/.openclaw/workspace/大力水手/
```
需要下载整个文件夹到电脑

### 2. 安装 Android Studio
如果还没安装：
1. 访问 https://developer.android.com/studio
2. 下载并安装
3. 首次打开时会下载SDK，耐心等待

---

## 详细构建步骤

### 步骤1: 打开项目

1. 打开 Android Studio
2. 点击 **Open**（不要选 New Project）
   ![打开项目](https://i.imgur.com/placeholder1.png)
3. 浏览找到下载的 `大力水手/android` 文件夹
4. 点击 **OK**

---

### 步骤2: 等待 Gradle 同步

打开后，Android Studio 会自动下载依赖，需要等待：

**您会看到：**
- 底部状态栏显示 "Gradle: Build Running"
- 右上角有进度条在转

**等待时间：** 5-15分钟（第一次会比较久）

**完成后：** 左下角显示 "Gradle sync finished"

**⚠️ 注意：** 如果提示下载失败，检查网络连接

---

### 步骤3: 构建 Release APK

1. 点击顶部菜单栏：
   ```
   Build → Generate Signed Bundle/APK...
   ```
   ![构建菜单](https://i.imgur.com/placeholder2.png)

2. 弹窗中选择 **APK**：
   - 选中 APK
   - 点击 **Next**

---

### 步骤4: 填写签名信息

这是关键步骤，仔细填写：

**界面显示：**
```
Key store path:     [        ] 📁
Key store password: [        ]
Key alias:          [        ]
Key password:       [        ]
```

**按下面填写：**

1. **Key store path**: 
   - 点击右侧 📁 按钮
   - 浏览找到 `dalishuishou.keystore` 文件
   - 点击 **OK**

2. **Key store password**:
   - 输入: `Dalishuishou2026!`

3. **Key alias**:
   - 输入: `dalishuishou`

4. **Key password**:
   - 输入: `Dalishuishou2026!`

5. 勾选 **Remember passwords**（记住密码）

6. 点击 **Next**

---

### 步骤5: 选择构建类型

**界面显示：**
```
Build Variants:  [debug ▼]
Destination Folder: app/release
```

**操作：**

1. 点击 **Build Variants** 下拉框
2. 选择 **release**
3. 确认 **Destination Folder** 是 `app/release`
4. 点击 **Finish**

---

### 步骤6: 等待构建完成

**您会看到：**
- 底部状态栏显示 "Building APK..."
- 进度条在走

**等待时间：** 3-10分钟

**构建成功后：**
- 右下角弹出提示: "APK(s) generated successfully"
- 点击提示中的 "locate" 可以直接打开文件夹

---

### 步骤7: 找到 APK 文件

构建成功后，APK 文件在：
```
大力水手/android/app/release/app-release.apk
```

**文件大小：** 约 20-30 MB

---

## ✅ 构建完成！

现在您可以：

1. **在手机上安装测试**
   - 把 APK 传到手机
   - 点击安装
   - 测试所有功能

2. **上传到应用商店**
   - 华为、小米、腾讯等平台

---

## 🔧 常见问题

### Q: Gradle 同步失败怎么办？
**A:** 
1. 检查网络连接
2. 点击右上角小象图标 "Sync Project with Gradle Files"
3. 或者 File → Sync Project with Gradle Files

### Q: 提示 "SDK not found"?
**A:**
1. 点击提示的链接下载 SDK
2. 或者 File → Settings → Appearance & Behavior → System Settings → Android SDK
3. 安装所需的 SDK 版本

### Q: 构建失败显示红色错误？
**A:**
1. 点击 Build → Clean Project
2. 然后 Build → Rebuild Project
3. 再试一次 Generate Signed APK

### Q: 找不到 dalishuishou.keystore？
**A:**
1. 确认文件在项目 `大力水手/android/` 文件夹下
2. 检查文件名是否正确

---

## 📱 构建成功后测试

在手机上安装 APK 后，测试这些功能：
- [ ] 登录是否正常
- [ ] 订单列表能否加载
- [ ] 订单详情是否显示
- [ ] 扫码功能是否正常
- [ ] 地图能否显示
- [ ] 推送能否接收

---

**有问题随时截图发我！** 💫
