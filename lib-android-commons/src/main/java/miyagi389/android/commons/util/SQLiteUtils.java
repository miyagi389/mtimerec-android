package miyagi389.android.commons.util;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public final class SQLiteUtils {

    private SQLiteUtils() {
    }

    @SuppressWarnings("WeakerAccess")
    public static void execSQL(
        @NonNull final SQLiteDatabase db,
        @NonNull final String sql
    ) {
        db.execSQL(sql);
    }

    public static void execSQL(
        @NonNull final SQLiteDatabase db,
        @NonNull final String[] sqls
    ) {
        for (final String sql : sqls) {
            execSQL(db, sql);
        }
    }
}
