# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Dev\SDK/tools/proguard/proguard-android.txt
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

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

#tencent bugly
-keep public class com.tencent.bugly.**{*;}

-dontwarn com.activeandroid.**
-keep class com.activeandroid.** {*; }
-keep interface com.activeandroid.** {*; }

-dontwarn com.zhaoyan.ladderball.model.**
-keep class com.zhaoyan.ladderball.model.** {*; }
-keep interface com.zhaoyan.ladderball.model.** {*; }

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn com.squareup.okhttp.**