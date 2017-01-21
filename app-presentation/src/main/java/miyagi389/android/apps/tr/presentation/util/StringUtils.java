package miyagi389.android.apps.tr.presentation.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * guava library - com.google.common.base.Strings から必要な部分を移植。
 * <p>guava はメソッド数が多く、 Androidアプリのメソッド数が65kを超えられない制限にかかる可能性が高くなるので
 * 必要な部分のメソッドだけ移植して使用する。</p>
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Returns the given string if it is non-null; the empty string otherwise.
     *
     * @param s the string to test and possibly return
     * @return {@code string} itself if it is non-null; {@code ""} if it is null
     */
    @NonNull
    public static String nullToEmpty(@Nullable final String s) {
        return (s == null) ? "" : s;
    }
}
