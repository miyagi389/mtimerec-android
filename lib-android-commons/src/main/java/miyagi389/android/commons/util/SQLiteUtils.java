package miyagi389.android.commons.util;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public final class SQLiteUtils {

    private SQLiteUtils() {
    }

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
        final int length = sqls.length;
        for (int i = 0; i < length; i++) {
            execSQL(db, sqls[i]);
        }
    }
}
