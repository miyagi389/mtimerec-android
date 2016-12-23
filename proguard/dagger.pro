-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection

-dontwarn dagger.internal.codegen.**
-dontwarn dagger.shaded.auto.common.**

-dontnote dagger.internal.**
-dontnote dagger.shaded.auto.common.**
