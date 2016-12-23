package miyagi389.android.commons.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public abstract class AbstractProvider extends ContentProvider {

    @Override
    public abstract boolean onCreate();

    @Override
    public abstract String getType(@NonNull final Uri uri);

    @NonNull
    protected abstract SQLiteDatabase getWritableDatabase();

    @NonNull
    protected abstract SQLiteDatabase getReadableDatabase();

    protected abstract Cursor queryInternal(
        @NonNull SQLiteDatabase db,
        @NonNull Uri uri,
        @Nullable String[] project,
        @Nullable String selection,
        @Nullable String[] selectionArgs,
        @Nullable String sortOrder
    );

    protected abstract Uri insertInternal(
        @NonNull SQLiteDatabase db,
        @NonNull Uri uri,
        @NonNull ContentValues values
    );

    protected abstract int updateInternal(
        @NonNull SQLiteDatabase db,
        @NonNull Uri uri,
        @NonNull ContentValues values,
        @Nullable String selection,
        @Nullable String[] selectionArgs
    );

    protected abstract int deleteInternal(
        @NonNull SQLiteDatabase db,
        @NonNull Uri uri,
        @Nullable String selection,
        @Nullable String[] selectionArgs
    );

    @Override
    public int bulkInsert(
        @NonNull final Uri uri,
        @NonNull final ContentValues[] values
    ) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            final int numValues = values.length;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < numValues; i++) {
                insertInternal(db, uri, values[i]);
            }
            db.setTransactionSuccessful();
            return numValues;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Cursor query(
        @NonNull final Uri uri,
        final String[] projection,
        final String selection,
        final String[] selectionArgs,
        final String sortOrder
    ) {
        final SQLiteDatabase db = getReadableDatabase();
        return queryInternal(db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Uri insert(
        @NonNull final Uri uri,
        final ContentValues values
    ) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            final Uri ret = insertInternal(db, uri, values);
            db.setTransactionSuccessful();
            return ret;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int update(
        @NonNull final Uri uri,
        final ContentValues values,
        final String selection,
        final String[] selectionArgs
    ) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            final int ret = updateInternal(db, uri, values, selection, selectionArgs);
            db.setTransactionSuccessful();
            return ret;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(
        @NonNull final Uri uri,
        final String selection,
        final String[] selectionArgs
    ) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            final int ret = deleteInternal(db, uri, selection, selectionArgs);
            db.setTransactionSuccessful();
            return ret;
        } finally {
            db.endTransaction();
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(
        @NonNull final ArrayList<ContentProviderOperation> operations
    )
        throws OperationApplicationException {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            final ContentProviderResult[] ret = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return ret;
        } finally {
            db.endTransaction();
        }
    }
}
