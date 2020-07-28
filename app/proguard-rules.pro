# Preserve annotations, line numbers, and source file names
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keepattributes Signature
-keepattributes Exceptions

-keep class com.shortstack.hackertracker.network.** { *; }
-keep class com.shortstack.hackertracker.models.** { *; }
-keep class com.shortstack.hackertracker.ui.themes.** { *; }

-dontwarn com.shortstack.hackertracker.views.**

-keep class com.shortstack.hackertracker.ui.home.HomeFragment { *; }
-keep class com.shortstack.hackertracker.ui.schedule.ScheduleFragment { *; }
-keep class com.shortstack.hackertracker.ui.maps.MapsFragment { *; }
-keep class com.shortstack.hackertracker.ui.information.faq.FAQFragment { *; }
-keep class com.shortstack.hackertracker.ui.information.vendors.VendorsFragment { *; }
-keep class com.shortstack.hackertracker.ui.search.SearchFragment { *; }
-keep class com.shortstack.hackertracker.ui.settings.SettingsFragment { *; }

-keep class android.support.v7.widget.SearchView { *; }

# Parceler configuration
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.Parceler$$Parcels