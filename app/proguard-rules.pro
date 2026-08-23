# Proguard rules for Criterio
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.urielhuerta.criterio.data.** { *; }
-keep class com.urielhuerta.criterio.domain.** { *; }
