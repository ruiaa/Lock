# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android Studio\SDK/tools/proguard/proguard-android.txt
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

##指定代码的压缩级别
 -optimizationpasses 5
 #包明不混合大小写
 -dontusemixedcaseclassnames
 #不去忽略非公共的库类
 -dontskipnonpubliclibraryclasses
  #优化  不优化输入的类文件
 -dontoptimize
  #预校验
 -dontpreverify
  #混淆时是否记录日志
 -verbose
  # 混淆时所采用的算法
 -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
 #保护注解
 -keepattributes *Annotation*
 # 保持哪些类不被混淆
 -keep public class * extends android.app.Fragment
 -keep public class * extends android.app.Activity
 -keep public class * extends android.app.Application
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 -keep public class * extends android.app.backup.BackupAgentHelper
 -keep public class * extends android.preference.Preference
 -keep public class com.android.vending.licensing.ILicensingService

 #友盟
 # 以下类过滤不混淆
 -keep public class * extends com.umeng.**
 # 以下包不进行过滤
  -keep class com.umeng.**{*;}
  -keepclassmembers class * {
     public <init> (org.json.JSONObject);
  }
  -keep public class com.chenji.lock.R$*{
  public static final int *;
  }
  -keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
  }
-dontwarn com.umeng.**
-keep public interface com.umeng.analytics.**
-keep public class com.umeng.analytics.* {*;}


#广点通
  -keep class com.qq.e.** {
   public protected *;
   }
   -keep class android.support.v4.app.NotificationCompat**{
   public *;
   }

#图表
   -keep class com.github.mikephil.charting.** { *; }
 -dontwarn io.realm.**

#阿里百川
-keep class com.alibaba.** { *;}
-dontwarn com.alibaba.**
