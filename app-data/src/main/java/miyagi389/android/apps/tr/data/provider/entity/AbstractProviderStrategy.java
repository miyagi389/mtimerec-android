package miyagi389.android.apps.tr.data.provider.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract class AbstractProviderStrategy {

    @NonNull
    public String getType(@NonNull final Uri uri) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @NonNull
    public Cursor query(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        @Nullable final String[] projection,
        @Nullable final String selection,
        @Nullable final String[] selectionArgs,
        @Nullable final String sortOrder
    ) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Nullable
    public Uri insert(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        @NonNull final ContentValues values
    ) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    public int update(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        @Nullable final ContentValues values,
        @Nullable final String where,
        @Nullable final String[] whereArgs
    ) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    public int delete(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        @Nullable final String where,
        @Nullable final String[] whereArgs
    ) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
}
