# remove android.util.Log.d and android.util.Log.v
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
