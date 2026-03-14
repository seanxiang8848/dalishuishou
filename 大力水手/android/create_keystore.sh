#!/bin/bash

# 大力水手 - 一键创建签名并获取SHA1
# 保存为: create_keystore.sh

echo "🚀 大力水手签名生成工具"
echo "========================"
echo ""

# 检查keytool
if ! command -v keytool &> /dev/null; then
    echo "❌ 错误: 未找到keytool命令"
    echo ""
    echo "请安装Java JDK:"
    echo "  Windows: https://adoptium.net/"
    echo "  Mac: brew install openjdk"
    echo "  Linux: sudo apt install default-jdk"
    exit 1
fi

# 设置变量
KEYSTORE_FILE="dalishuishou.keystore"
ALIAS="dalishuishou"
STORE_PASS="Dalishuishou2026!"
KEY_PASS="Dalishuishou2026!"
VALIDITY=10000

# 检查是否已存在
if [ -f "$KEYSTORE_FILE" ]; then
    echo "⚠️  签名文件已存在: $KEYSTORE_FILE"
    read -p "是否覆盖? (y/n): " overwrite
    if [ "$overwrite" != "y" ]; then
        echo "取消操作"
        exit 0
    fi
    rm "$KEYSTORE_FILE"
fi

echo "📦 正在生成签名文件..."
echo ""

# 生成密钥库
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY" \
    -dname "CN=大力水手, OU=配送团队, O=大力水手科技, L=成都, ST=四川, C=CN" \
    -storepass "$STORE_PASS" \
    -keypass "$KEY_PASS"

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ 签名生成失败"
    exit 1
fi

echo ""
echo "✅ 签名文件创建成功!"
echo ""
echo "📂 文件位置: $(pwd)/$KEYSTORE_FILE"
echo ""

# 获取SHA1
echo "🔑 获取SHA1指纹..."
echo ""
echo "=============================="
keytool -list -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$ALIAS" \
    -storepass "$STORE_PASS" 2>/dev/null | grep -A 1 "Certificate fingerprints" | grep "SHA1"
echo "=============================="
echo ""

# 获取MD5
echo "🔑 获取MD5指纹..."
echo ""
echo "=============================="
keytool -list -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$ALIAS" \
    -storepass "$STORE_PASS" 2>/dev/null | grep -A 1 "Certificate fingerprints" | grep "MD5"
echo "=============================="
echo ""

# 保存配置
echo "📝 保存签名配置..."
cat > signing.properties << EOF
RELEASE_STORE_FILE=$KEYSTORE_FILE
RELEASE_STORE_PASSWORD=$STORE_PASS
RELEASE_KEY_ALIAS=$ALIAS
RELEASE_KEY_PASSWORD=$KEY_PASS
EOF

echo "✅ 配置已保存到: $(pwd)/signing.properties"
echo ""
echo "⚠️  重要提醒:"
echo "   1. 请妥善保存 $KEYSTORE_FILE 文件"
echo "   2. 密码: $STORE_PASS"
echo "   3. 密钥丢失将无法更新应用"
echo "   4. 请将keystore文件备份到安全位置"
echo ""
echo "🚀 接下来执行: ./gradlew assembleRelease"
