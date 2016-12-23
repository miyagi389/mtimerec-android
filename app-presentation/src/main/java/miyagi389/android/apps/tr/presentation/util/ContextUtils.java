package miyagi389.android.apps.tr.presentation.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import timber.log.Timber;

public final class ContextUtils {

    private ContextUtils() {
    }

    /**
     * AndroidManifest.xml の android:versionName 属性の値を返す。
     */
    @NonNull
    public static String getAppVersionName(@NonNull final Context context) {
        String result;
        try {
            final PackageManager e = context.getPackageManager();
            final PackageInfo pi = e.getPackageInfo(context.getPackageName(), 0);
            result = pi.versionName;
        } catch (final PackageManager.NameNotFoundException ignore) {
            result = "";
        }
        return result;
    }

    /**
     * AndroidManifest.xml の android:versionCode 属性の値を返す。
     */
    public static int getAppVersionCode(@NonNull final Context context) {
        int result = -1;
        try {
            final PackageManager pm = context.getPackageManager();
            final PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            result = pi.versionCode;
        } catch (final PackageManager.NameNotFoundException e) {
            Timber.e(e, e.getMessage());
        }
        return result;
    }
}
