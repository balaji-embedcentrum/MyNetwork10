# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/AgmoStudio/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-keep class com.squareup.okhttp.** { *; }
-keep class com.parse.** { *; }
-keep class com.twitter.** { *; }
-libjars libs/Parse-1.11.0.jar

-keep class io.nlopez.smartlocation.**
-dontwarn io.nlopez.smartlocation.**

-keep class cn.pedant.SweetAlert.** { *; }

-keepattributes *Annotation*
-keep public class * extends android.support.design.widget.CoordinatorLayout.Behavior { *; }
-keep public class * extends android.support.design.widget.ViewOffsetBehavior { *; }
-keep public class com.agmostudio.util.ScrollAwareFABBehavior { *; }

#App specific configs
-keep class com.networkstudent.model.** { *; }

#workaround Issue 78377
-keep class !android.support.v7.internal.view.menu.MenuBuilder
-keep class !android.support.v7.internal.view.menu.SubMenuBuilder

-dontwarn android.util.FloatMath
-dontwarn android.app.Notification

-keep class android.support.v7.widget.SearchView { *; }

# Remove log
-assumenosideeffects class com.agmostudio.util.Ln {
    public static int i(...);
    public static int w(...);
    public static int v(...);
    public static int d(...);
    public static int e(...);
}

# Butter Knife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Ion
-keep class com.koushikdutta.** { *; }
-dontwarn com.koushikdutta.**

# Joda Time
-dontwarn org.joda.convert.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# Joda-Time-Android
-dontwarn org.joda.time.tz.ZoneInfoCompiler

# EventBus
-dontwarn de.greenrobot.event.util.*$Support
-dontwarn de.greenrobot.event.util.*$SupportManagerFragment
-keepclassmembers class ** {
    public void onEvent*(**);
    public void onHandle*(**);
}

# Android Priority JobQueue
-keep class com.path.android.jobqueue.** { *; }
-dontwarn com.path.android.jobqueue.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

##---------------End: proguard configuration for Gson  ----------
# we need line numbers in our stack traces otherwise they are pretty useless
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable


###---------------Begin: proguard configuration for ACRA  ----------
## ACRA needs "annotations" so add this...
#-keepattributes *Annotation*
#
## keep this class so that logging will show 'ACRA' and not a obfuscated name like 'a'.
## Note: if you are removing log messages elsewhere in this file then this isn't necessary
#-keep class org.acra.ACRA {
#	*;
#}
#
## keep this around for some enums that ACRA needs
#-keep class org.acra.ReportingInteractionMode {
#    *;
#}
#
#-keepnames class org.acra.sender.HttpSender$** {
#    *;
#}
#
#-keepnames class org.acra.ReportField {
#    *;
#}
#
## keep this otherwise it is removed by ProGuard
#-keep public class org.acra.ErrorReporter
#{
#    public void addCustomData(java.lang.String,java.lang.String);
#    public void putCustomData(java.lang.String,java.lang.String);
#    public void removeCustomData(java.lang.String);
#}
#
## keep this otherwise it is removed by ProGuard
#-keep public class org.acra.ErrorReporter
#{
#    public void handleSilentException(java.lang.Throwable);
#}
#
## Keep the support library
#-keep class org.acra.** { *; }
#-keep interface org.acra.** { *; }
###---------------End: proguard configuration for ACRA  ----------

