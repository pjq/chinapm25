# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/pengjianqing/Documents/Android/android-sdk-macosx/tools/proguard/proguard-android.txt
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

-repackageclasses

-dontwarn org.springframework.**
-dontwarn com.google.common.**
-dontwarn sun.misc.Unsafe

# Keep Options:

-keepattributes *Annotation*
-keepattributes JavascriptInterface
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keep class com.google.gson.** {*;}
-keep class com.google.common.** {*;}
-keep class com.loopj.android.http.** {*;}
-keep class sun.misc.Unsafe{*;}
-keep public class * { public protected *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.Service
-keep public class * extends android.view.View
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application
-keep public class * extends android.app.Dialog
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService
-keep class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *; }

-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet, int); }
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet); }
-keepclasseswithmembernames class * { native <methods>; }

-keepclassmembers class * extends android.app.Activity { public void *(android.view.View); }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Provide public *;
}

# logback-android and SLF4J
-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }
-dontwarn ch.qos.logback.core.net.*

# joda-time
-keep class org.joda.time.** {*;}
-dontwarn org.joda.time.**


-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**