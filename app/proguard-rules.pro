# Hermes Android ProGuard rules

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class kotlinx.serialization.** { *; }

# kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Coil
-keep class coil.** { *; }

# Glance
-keep class androidx.glance.** { *; }

# Keep data classes used in JSON for Hermes API (tolerant)
-keep class com.hermes.android.data.api.dto.** { *; }

# General
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
