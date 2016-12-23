#-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

#-keep,allowoptimization class com.google.inject.** { *; }

-dontnote sun.misc.Unsafe

# android-sdk/platforms/android-23/optional/org.apache.http.legacy.jar
-dontnote android.net.http.**
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
