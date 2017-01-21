package miyagi389.android.apps.tr.presentation.util;

import android.support.annotation.NonNull;

/**
 * guava library - com.google.common.primitives.Ints から必要な部分を移植。
 * <p>guava はメソッド数が多く、 Androidアプリのメソッド数が65kを超えられない制限にかかる可能性が高くなるので
 * 必要な部分のメソッドだけ移植して使用する。</p>
 */
public final class IntegerUtils {

    private IntegerUtils() {
    }

    public static int tryParse(
        @NonNull final String s,
        final int defaultValue
    ) {
        try {
            return Integer.parseInt(s);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }
}
