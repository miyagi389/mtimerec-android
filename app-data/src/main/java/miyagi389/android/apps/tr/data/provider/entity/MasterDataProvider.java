package miyagi389.android.apps.tr.data.provider.entity;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import miyagi389.android.commons.provider.AbstractProvider;
import miyagi389.android.commons.util.SQLiteUtils;

public class MasterDataProvider extends AbstractProvider {

    public static final String AUTHORITY = MasterDataProvider.class.getPackage().getName() + ".master";

    private static final String DATABASE_NAME = "main.db";

    /**
     * @since 1.0.0
     */
    @SuppressWarnings("UnusedDeclaration")
    private static final int DATABASE_VERSION_1_0_0 = 1;

    private static final int DATABASE_VERSION = DATABASE_VERSION_1_0_0;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(
            @NonNull final Context context,
            @NonNull final String dbName
        ) {
            super(context, dbName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL(TemplateEntity.Schema.CREATE_TABLE);
            SQLiteUtils.execSQL(db, TemplateEntity.Schema.CREATE_INDEX);
        }

        @Override
        public void onUpgrade(
            final SQLiteDatabase db,
            final int oldVersion,
            final int newVersion
        ) {
        }

        @Override
        public void onOpen(final SQLiteDatabase db) {
        }
    }

    @Override
    public boolean onCreate() {
        //noinspection ConstantConditions
        mHelper = new DatabaseHelper(getContext(), DATABASE_NAME);
        return true;
    }

    @NonNull
    @Override
    public SQLiteDatabase getWritableDatabase() {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        return db;
    }

    @NonNull
    @Override
    public SQLiteDatabase getReadableDatabase() {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        return db;
    }

    @Override
    public String getType(@NonNull final Uri uri) {
        return getUriPattern(uri).strategy.getType(uri);
    }

    @Override
    public Cursor queryInternal(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        final String[] projection,
        final String selection,
        final String[] selectionArgs,
        final String sortOrder
    ) {
        return getUriPattern(uri).strategy.query(db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Uri insertInternal(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        @NonNull final ContentValues values
    ) {
        return getUriPattern(uri).strategy.insert(db, uri, values);
    }

    @Override
    public int updateInternal(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        @NonNull final ContentValues values,
        final String selection,
        final String[] selectionArgs
    ) {
        return getUriPattern(uri).strategy.update(db, uri, values, selection, selectionArgs);
    }

    @Override
    public int deleteInternal(
        @NonNull final SQLiteDatabase db,
        @NonNull final Uri uri,
        final String selection,
        final String[] selectionArgs
    ) {
        return getUriPattern(uri).strategy.delete(db, uri, selection, selectionArgs);
    }

    @NonNull
    private UriPattern getUriPattern(@NonNull final Uri uri) throws IllegalArgumentException {
        final UriPattern type = UriPattern.get(sUriMatcher.match(uri));
        if (type == null) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return type;
    }

    private enum UriPattern {
        TEMPLATE(new TemplateEntity.ProviderStrategy()),
        TEMPLATE_ID(new TemplateEntity.ProviderStrategyId());

        public AbstractProviderStrategy strategy;

        UriPattern(@NonNull final AbstractProviderStrategy strategy) {
            this.strategy = strategy;
        }

        @Nullable
        public static UriPattern get(final int ordinal) {
            final UriPattern[] values = values();
            final int length = values.length;
            if (ordinal < 0 || length <= ordinal) {
                return null;
            }
            return values[ordinal];
        }
    }

    static {
        // @formatter:off
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITY, TemplateEntity.Schema.NAME, UriPattern.TEMPLATE.ordinal());
        sUriMatcher.addURI(AUTHORITY, TemplateEntity.Schema.NAME + "/#", UriPattern.TEMPLATE_ID.ordinal());

        // @formatter:on
    }
}
