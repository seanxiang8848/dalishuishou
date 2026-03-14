# ProGuard混淆规则

# 基本规则
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# 保持Compose相关
-keep class androidx.compose.** { *; }
-keep class androidx.compose.material3.** { *; }

# 保持数据类
-keep class com.seanxiangchao.orders.data.model.** { *; }
-keepclassmembers class com.seanxiangchao.orders.data.model.** {
    <fields>;
}

# 保持序列化类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Gson
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.reflect.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Coil
-keep class coil.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# 极光推送
-dontoptimize
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class cn.jiguang.** { *; }

# 高德地图
-dontwarn com.amap.api.**
-keep class com.amap.api.** { *; }
-keep class com.autonavi.** { *; }

# ZXing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }

# Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# 移除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 压缩优化
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
