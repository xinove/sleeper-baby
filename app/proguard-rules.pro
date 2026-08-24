# Sleeper Baby — R8/ProGuard (release)

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault,*Annotation*

-keep class com.sleeperbaby.app.SleeperBabyApplication { *; }
-keep class com.sleeperbaby.app.service.SleepRadioService { *; }
-keep class com.sleeperbaby.app.MainActivity { *; }

-keep class androidx.media.** { *; }
-keep class android.support.v4.media.** { *; }

-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.**
