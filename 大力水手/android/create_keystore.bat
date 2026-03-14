@echo off
chcp 65001
cls

echo 🚀 大力水手签名生成工具
echo ========================
echo.

REM 检查keytool
keytool -help > nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到keytool命令
    echo.
    echo 请安装Java JDK:
    echo   1. 访问 https://adoptium.net/
    echo   2. 下载并安装JDK
    echo   3. 重启命令提示符
    pause
    exit /b 1
)

REM 设置变量
set KEYSTORE_FILE=dalishuishou.keystore
set ALIAS=dalishuishou
set STORE_PASS=Dalishuishou2026!
set KEY_PASS=Dalishuishou2026!
set VALIDITY=10000

REM 检查是否已存在
if exist "%KEYSTORE_FILE%" (
    echo ⚠️ 签名文件已存在: %KEYSTORE_FILE%
    set /p OVERWRITE="是否覆盖? (y/n): "
    if /i not "%OVERWRITE%"=="y" (
        echo 取消操作
        pause
        exit /b 0
    )
    del "%KEYSTORE_FILE%"
)

echo 📦 正在生成签名文件...
echo.

REM 生成密钥库
keytool -genkey -v ^
    -keystore "%KEYSTORE_FILE%" ^
    -alias "%ALIAS%" ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity "%VALIDITY%" ^
    -dname "CN=大力水手, OU=配送团队, O=大力水手科技, L=成都, ST=四川, C=CN" ^
    -storepass "%STORE_PASS%" ^
    -keypass "%KEY_PASS%"

if errorlevel 1 (
    echo.
    echo ❌ 签名生成失败
    pause
    exit /b 1
)

echo.
echo ✅ 签名文件创建成功!
echo.
echo 📂 文件位置: %CD%\%KEYSTORE_FILE%
echo.

REM 获取SHA1
echo 🔑 获取SHA1指纹...
echo.
echo ==============================
keytool -list -v ^
    -keystore "%KEYSTORE_FILE%" ^
    -alias "%ALIAS%" ^
    -storepass "%STORE_PASS%" 2>nul | findstr "SHA1"
echo ==============================
echo.

REM 获取MD5
echo 🔑 获取MD5指纹...
echo.
echo ==============================
keytool -list -v ^
    -keystore "%KEYSTORE_FILE%" ^
    -alias "%ALIAS%" ^
    -storepass "%STORE_PASS%" 2>nul | findstr "MD5"
echo ==============================
echo.

REM 保存配置
echo 📝 保存签名配置...
(
echo RELEASE_STORE_FILE=%KEYSTORE_FILE%
echo RELEASE_STORE_PASSWORD=%STORE_PASS%
echo RELEASE_KEY_ALIAS=%ALIAS%
echo RELEASE_KEY_PASSWORD=%KEY_PASS%
) > signing.properties

echo ✅ 配置已保存到: %CD%\signing.properties
echo.
echo ⚠️ 重要提醒:
echo    1. 请妥善保存 %KEYSTORE_FILE% 文件
echo    2. 密码: %STORE_PASS%
echo    3. 密钥丢失将无法更新应用
echo    4. 请将keystore文件备份到安全位置
echo.
echo 🚀 接下来执行: gradlew assembleRelease
echo.
pause
