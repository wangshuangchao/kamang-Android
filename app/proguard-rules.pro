# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android\android-studio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5       # 指定代码的压缩级别
-dontusemixedcaseclassnames # 是否使用大小写混合
# 是否混淆第三方jar
-dontskipnonpubliclibraryclasses
-dontpreverify              # 混淆时是否做预校验
-verbose                    # 混淆时是否记录日志
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* # 混淆时所采用的算法
-dontwarn
-dontskipnonpubliclibraryclassmembers

-dontobfuscate
-dontoptimize

-keep public class * extends android.app.Application {*;}
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ButterKnife 混淆代码
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Glide 混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Deprecated,LocalVariable*Table,Synthetic,EnclosingMethod
-keepattributes EnclosingMethod

# FastJson 混淆代码
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# OkHttp 混淆代码
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

-keep class com.zhy.** { *; }
-dontwarn com.zhy.**

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *;}

-dontwarn com.aliyun.**
-keep class com.aliyun.** { *;}

-dontwarn com.aliyun.**
-keep class com.aliyun.** { *;}

-dontwarn rx.internal.util.**
-keep class rx.internal.util.** { *;}

-dontwarn uk.co.senab.**
-keep class uk.co.senab.** { *;}

-keep class org.apache.**{*;}                                               #过滤commons-httpclient-3.1.jar

-keep class com.fasterxml.jackson.**{*;}                                    #过滤jackson-core-2.1.4.jar等

-dontwarn com.lidroid.xutils.**                                             #去掉警告
-keep class com.lidroid.xutils.**{*;}                                       #过滤xUtils-2.6.14.jar
-keep class * extends java.lang.annotation.Annotation{*;}                   #这是xUtils文档中提到的过滤掉注解

# 百度地图（jar包换成自己的版本，记得签名要匹配）
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class com.sinovoice.** {*;}
-keep class pvi.com.** {*;}
-dontwarn com.baidu.**
-dontwarn vi.com.**
-dontwarn pvi.com.**

-dontwarn com.android.okhttp.**
-keep class com.android.okhttp.** { *;}

-dontwarn com.gionee.**
-keep class com.gionee.** { *;}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

#友盟相关
-dontwarn com.umeng.**
-keep class com.umeng.** { *; }

#adapter也不能混淆
-keep public class * extends android.widget.BaseAdapter {*;}

#如果引用了v4或者v7包
-dontwarn android.support.**

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#关于数据库
#保留数据库驱动--数据库驱动往往是动态加载的
-keep class * implements java.sql.Driver

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-dontwarn com.activeandroid.**
-keep class com.activeandroid.** { *; }
-dontwarn com.mugua.jiguang.chat.**
-keep class com.mugua.jiguang.chat.** { *; }
-dontwarn com.lidong.**
-keep class com.lidong.** { *; }

-keep class com.mugua.enterprise.util.BitmapAndStringUtils{ *; }
-keep class com.mugua.enterprise.activity.me.** { *; }
-keep class com.mugua.enterprise.model.** { *; }
-keep class com.mugua.enterprise.bean.** { *; }
-keep class com.mugua.videoplayer.model.** { *; }

-dontwarn demo.**
-keep class demo.** { *;}

-keep class **.R$* {
 *;
}

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#-ignorewarnings
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Deprecated,LocalVariable*Table,Synthetic,EnclosingMethod
-keepattributes EnclosingMethod

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}


-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}


-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * implements java.io.Serializable {
    public *;
}

-keep class **.R$*{
    *;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keepclasseswithmembernames class * {
native <methods>;
}

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}


-keep class com.shuyu.gsyvideoplayer.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**