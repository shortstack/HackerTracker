# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Preserve annotations, line numbers, and source file names
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keepattributes Signature
-keepattributes Exceptions

-keep class com.advice.schedule.network.** { *; }
-keep class com.advice.schedule.models.** { *; }
-keep class com.advice.schedule.ui.themes.** { *; }
-dontwarn com.advice.schedule.views.**

-keep class com.advice.schedule.ui.home.HomeFragment { *; }
-keep class com.advice.schedule.ui.schedule.ScheduleFragment { *; }
-keep class com.advice.schedule.ui.maps.MapsFragment { *; }
-keep class com.advice.schedule.ui.information.faq.FAQFragment { *; }
-keep class com.advice.schedule.ui.information.vendors.VendorsFragment { *; }
-keep class com.advice.schedule.ui.search.SearchFragment { *; }
-keep class com.advice.schedule.ui.settings.SettingsFragment { *; }

# AndroidPdfViewer
-keep class com.shockwave.**

-keep class android.support.v7.widget.SearchView { *; }

# Parceler configuration
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.Parceler$$Parcels