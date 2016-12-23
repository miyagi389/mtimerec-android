package miyagi389.android.apps.tr.data.provider.entity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class TemplateEntity implements Parcelable {

    /**
     * The content:// style URL for this table
     */
    @SuppressWarnings("WeakerAccess")
    public static final Uri CONTENT_URI = Uri.parse("content://" + MasterDataProvider.AUTHORITY + "/" + Schema.NAME);

    /**
     * The MIME type of {@link #CONTENT_URI}.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
        + MasterDataProvider.AUTHORITY
        + "."
        + Schema.NAME;

    /**
     * The MIME type of a {@link #CONTENT_URI}.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
        + MasterDataProvider.AUTHORITY
        + "."
        + Schema.NAME;

    /**
     * column strings
     */
    public interface Columns extends BaseColumns {

        /**
         * 登録先カレンダーID
         * <p>Type: int</p>
         */
        String _CALENDAR_ID = "calendar_id";

        /**
         * 登録先カレンダー追加時のイベントタイトル
         * <p>Type: text</p>
         */
        String _EVENT_TITLE = "event_title";
    }

    /* package */interface Schema {

        String NAME = "template";

        String CREATE_TABLE = "CREATE TABLE "
            + NAME
            + " ("
            + Columns._ID
            + " INTEGER PRIMARY KEY, "
            + Columns._CALENDAR_ID
            + " INTEGER NOT NULL, "
            + Columns._EVENT_TITLE
            + " TEXT NOT NULL); ";

        String[] CREATE_INDEX = {};
    }

    public static class CursorWrapper extends android.database.CursorWrapper {

        private final int mColumnIndexId;
        private final int mColumnIndexCalendarId;
        private final int mColumnIndexEventTitle;

        public CursorWrapper(@NonNull final Cursor cursor) {
            super(cursor);
            mColumnIndexId = getColumnIndex(Columns._ID);
            mColumnIndexCalendarId = getColumnIndex(Columns._CALENDAR_ID);
            mColumnIndexEventTitle = getColumnIndex(Columns._EVENT_TITLE);
        }

        @NonNull
        public TemplateEntity toEntity() {
            final TemplateEntity o = new TemplateEntity();
            if (mColumnIndexId != -1) {
                o.id = getId();
            }
            if (mColumnIndexCalendarId != -1) {
                o.calendarId = getCalendarId();
            }
            if (mColumnIndexEventTitle != -1) {
                o.eventTitle = getEventTitle();
            }
            return o;
        }

        public long getId() {
            return getLong(mColumnIndexId);
        }

        public long getCalendarId() {
            return getLong(mColumnIndexCalendarId);
        }

        @NonNull
        public String getEventTitle() {
            return getString(mColumnIndexEventTitle);
        }
    }

    public static final class Utils {

        private Utils() {
        }

        @Nullable
        public static CursorWrapper toCursorWrapperByAll(
            @NonNull final Context context,
            @Nullable final String sortOrder
        ) {
            final Uri uri = CONTENT_URI;
            final ContentResolver cr = context.getContentResolver();
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, null, null, null, sortOrder);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }

        @Nullable
        public static CursorWrapper toCursorWrapperById(
            @NonNull final Context context,
            final long id
        ) {
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            final ContentResolver cr = context.getContentResolver();
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }

        public static Uri insert(
            @NonNull final Context context,
            @NonNull final TemplateEntity entity
        ) {
            final Uri uri = CONTENT_URI;
            final ContentResolver cr = context.getContentResolver();
            return cr.insert(uri, entity.toContentValues());
        }

        public static int update(
            @NonNull final Context context,
            @NonNull final TemplateEntity entity
        ) {
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI, entity.id);
            final ContentResolver cr = context.getContentResolver();
            return cr.update(uri, entity.toContentValues(), null, null);
        }

        public static int deleteById(
            @NonNull final Context context,
            final long id
        ) {
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            final ContentResolver cr = context.getContentResolver();
            return cr.delete(uri, null, null);
        }
    }

    @NonNull
    public ContentValues toContentValues() {
        final ContentValues v = new ContentValues();
        if (id > 0) {
            v.put(Columns._ID, id);
        }
        v.put(Columns._EVENT_TITLE, eventTitle);
        v.put(Columns._CALENDAR_ID, calendarId);
        return v;
    }

    /*package*/static class ProviderStrategy extends AbstractProviderStrategy {

        @NonNull
        @Override
        public String getType(@NonNull final Uri uri) {
            return CONTENT_TYPE;
        }

        @NonNull
        @Override
        public Cursor query(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @Nullable final String[] projection,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs,
            @Nullable final String sortOrder
        ) {
            final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(Schema.NAME);
            return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        }

        @Nullable
        @Override
        public Uri insert(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @NonNull final ContentValues values
        ) {
            final long id = db.insert(Schema.NAME, null, values);
            return (id == -1) ? null : ContentUris.withAppendedId(CONTENT_URI, id);
        }

        @Override
        public int update(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @Nullable final ContentValues values,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs
        ) {
            return db.update(Schema.NAME, values, selection, selectionArgs);
        }

        @Override
        public int delete(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs
        ) {
            return db.delete(Schema.NAME, selection, selectionArgs);
        }
    }

    /*package*/ static class ProviderStrategyId extends AbstractProviderStrategy {

        @NonNull
        @Override
        public String getType(@NonNull final Uri uri) {
            return CONTENT_ITEM_TYPE;
        }

        @NonNull
        @Override
        public Cursor query(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @Nullable final String[] projection,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs,
            @Nullable final String sortOrder
        ) {
            final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(Schema.NAME);
            qb.appendWhere(Columns._ID + "=" + uri.getLastPathSegment());
            return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        }

        @Override
        public int update(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @Nullable final ContentValues values,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs
        ) {
            // CHECKSTYLE:OFF MagicNumber
            final StringBuilder custom = new StringBuilder(!TextUtils.isEmpty(selection) ? selection.length() + 16 : 16);
            // CHECKSTYLE:ON
            custom.append(Columns._ID).append('=').append(uri.getLastPathSegment());
            if (!TextUtils.isEmpty(selection)) {
                custom.append(" AND (").append(selection).append(")");
            }
            return db.update(Schema.NAME, values, custom.toString(), selectionArgs);
        }

        @Override
        public int delete(
            @NonNull final SQLiteDatabase db,
            @NonNull final Uri uri,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs
        ) {
            // CHECKSTYLE:OFF MagicNumber
            final StringBuilder custom = new StringBuilder(!TextUtils.isEmpty(selection) ? selection.length() + 16 : 16);
            // CHECKSTYLE:ON
            custom.append(Columns._ID).append("=").append(uri.getLastPathSegment());
            if (!TextUtils.isEmpty(selection)) {
                custom.append(" AND (").append(selection).append(")");
            }
            return db.delete(Schema.NAME, custom.toString(), selectionArgs);
        }
    }

    public long id;
    public long calendarId;
    public String eventTitle;

    public TemplateEntity() {
    }

    @Override
    public String toString() {
        return "TemplateEntity{" +
            "id=" + id +
            ", calendarId=" + calendarId +
            ", eventTitle='" + eventTitle + '\'' +
            '}';
    }

    /**
     * {@link Parcelable} constructor
     */
    private TemplateEntity(final Parcel in) {
        id = in.readLong();
        calendarId = in.readLong();
        eventTitle = in.readString();
    }

    /**
     * {@link Parcelable#writeToParcel(Parcel, int)}
     */
    @Override
    public void writeToParcel(
        final Parcel dest,
        final int flags
    ) {
        dest.writeLong(id);
        dest.writeLong(calendarId);
        dest.writeString(eventTitle);
    }

    /**
     * {@link Parcelable#describeContents()}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@link Parcelable.Creator}
     */
    public static final Parcelable.Creator<TemplateEntity> CREATOR = new Parcelable.Creator<TemplateEntity>() {
        public TemplateEntity createFromParcel(final Parcel in) {
            return new TemplateEntity(in);
        }

        public TemplateEntity[] newArray(final int size) {
            return new TemplateEntity[size];
        }
    };
}
