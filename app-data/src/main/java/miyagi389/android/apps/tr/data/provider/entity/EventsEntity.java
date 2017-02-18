package miyagi389.android.apps.tr.data.provider.entity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import miyagi389.android.apps.tr.domain.repository.EventsRepository;

/**
 * {@link android.provider.CalendarContract.Events} wrapper
 */
public class EventsEntity implements Parcelable {

    public static class CursorWrapper extends android.database.CursorWrapper {

        private final int mColumnIndexId;
        private final int mColumnIndexCalendarId;
        private final int mColumnIndexTitle;
        private final int mColumnIndexDescription;
        private final int mColumnIndexDtStart;
        private final int mColumnIndexDtEnd;
        private final int mColumnIndexTimezone;

        public CursorWrapper(@NonNull final Cursor cursor) {
            super(cursor);
            mColumnIndexId = getColumnIndex(Events._ID);
            mColumnIndexCalendarId = getColumnIndex(Events.CALENDAR_ID);
            mColumnIndexTitle = getColumnIndex(Events.TITLE);
            mColumnIndexDescription = getColumnIndex(Events.DESCRIPTION);
            mColumnIndexDtStart = getColumnIndex(Events.DTSTART);
            mColumnIndexDtEnd = getColumnIndex(Events.DTEND);
            mColumnIndexTimezone = getColumnIndex(Events.EVENT_TIMEZONE);
        }

        @NonNull
        public EventsEntity toEntity() {
            final EventsEntity o = new EventsEntity();
            if (mColumnIndexId != -1) {
                o.id = getId();
            }
            if (mColumnIndexCalendarId != -1) {
                o.calendarId = getCalendarId();
            }
            if (mColumnIndexTitle != -1) {
                o.title = getTitle();
            }
            if (mColumnIndexDescription != -1) {
                o.description = getDescription(null);
            }
            if (mColumnIndexDtStart != -1) {
                o.dtStart = getDtStart();
            }
            if (mColumnIndexDtEnd != -1) {
                o.dtEnd = getDtEnd();
            }
            if (mColumnIndexTimezone != -1) {
                o.timezone = getTimezone();
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
        public String getTitle() {
            return getString(mColumnIndexTitle);
        }

        @SuppressWarnings("WeakerAccess")
        public boolean isNullDescription() {
            return isNull(mColumnIndexDescription);
        }

        @Nullable
        public String getDescription(@Nullable final String defaultValue) {
            if (isNullDescription()) {
                return defaultValue;
            }
            return getString(mColumnIndexDescription);
        }

        public long getDtStart() {
            return getLong(mColumnIndexDtStart);
        }

        public long getDtEnd() {
            return getLong(mColumnIndexDtEnd);
        }

        @NonNull
        public String getTimezone() {
            return getString(mColumnIndexTimezone);
        }
    }

    public static final class Utils {

        private Utils() {
        }

        @Nullable
        public static CursorWrapper toCursorWrapperById(
            @NonNull final Context context,
            final long id
        ) {
            final Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
            final ContentResolver cr = context.getContentResolver();
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }

        @Nullable
        public static CursorWrapper toCursorWrapperLastDtStart(
            @NonNull final Context context,
            final long calendarId,
            @NonNull final String title
        ) {
            final Uri uri = Events.CONTENT_URI;
            final String selection = Events.CALENDAR_ID + " = ? AND " +
                Events.TITLE + " = ? AND " +
                Events.RRULE + " IS NULL AND " +
                Events.ALL_DAY + " = 0 AND " +
                Events.DELETED + " = 0";
            final String[] selectionArgs = new String[]{
                Long.toString(calendarId),
                title
            };
            final String sortOrder = Events.DTSTART + " DESC LIMIT 1";
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, null, selection, selectionArgs, sortOrder);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }

        @Nullable
        public static CursorWrapper toCursorWrapperCalendarId(
            @NonNull final Context context,
            final long calendarId,
            @NonNull final String title,
            @NonNull final EventsRepository.SortOrder sortOrder
        ) {
            final Uri uri = Events.CONTENT_URI;
            final String selection = Events.CALENDAR_ID + " = ? AND " +
                Events.TITLE + " = ? AND " +
                Events.RRULE + " IS NULL AND " +
                Events.ALL_DAY + " = 0 AND " +
                Events.DELETED + " = 0";
            final String[] selectionArgs = new String[]{
                Long.toString(calendarId),
                title
            };
            final String querySortOrder = toSortOrder(sortOrder);
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, null, selection, selectionArgs, querySortOrder);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }

        @Nullable
        public static CursorWrapper toCursorWrapperCalendarId(
            @NonNull final Context context,
            final long calendarId,
            @NonNull final String title,
            final long fromDate,
            final long toDate,
            @NonNull final EventsRepository.SortOrder sortOrder
        ) {
            final Uri uri = Events.CONTENT_URI;
            final String selection = Events.CALENDAR_ID + " = ? AND " +
                Events.TITLE + " = ? AND " +
                Events.RRULE + " IS NULL AND " +
                Events.ALL_DAY + " = 0 AND " +
                Events.DELETED + " = 0 AND " +
                Events.DTSTART + " >= ? AND " +
                Events.DTSTART + " < ?";
            final String[] selectionArgs = new String[]{
                Long.toString(calendarId),
                title,
                Long.toString(fromDate),
                Long.toString(toDate),
            };
            final String querySortOrder = toSortOrder(sortOrder);
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, null, selection, selectionArgs, querySortOrder);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }

        @Nullable
        private static String toSortOrder(@NonNull final EventsRepository.SortOrder sortOrder) {
            switch (sortOrder) {
                case DT_START_ASCENDING:
                    return Events.DTSTART + " ASC";
                case DT_START_DESCENDING:
                    return Events.DTSTART + " DESC";
                default:
                    return null;
            }
        }

        public static Uri insert(
            @NonNull final Context context,
            @NonNull final EventsEntity entity
        ) {
            final Uri uri = Events.CONTENT_URI;
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            return cr.insert(uri, entity.toContentValues());
        }

        public static int update(
            @NonNull final Context context,
            @NonNull final EventsEntity entity
        ) {
            final Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, entity.id);
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            return cr.update(uri, entity.toContentValues(), null, null);
        }

        public static int deleteById(
            @NonNull final Context context,
            final long id
        ) {
            final Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            return cr.delete(uri, null, null);
        }
    }

    public long id;
    public long calendarId;
    public String title;
    public String description;
    public long dtStart;
    public long dtEnd;
    public String timezone;

    public EventsEntity() {
    }

    @Override
    public String toString() {
        return "EventsEntity{" +
            "id=" + id +
            ", calendarId=" + calendarId +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", dtStart=" + dtStart +
            ", dtEnd=" + dtEnd +
            ", timezone='" + timezone + '\'' +
            '}';
    }

    @NonNull
    public ContentValues toContentValues() {
        final ContentValues v = new ContentValues();
        if (!isNew()) {
            v.put(Events._ID, id);
        }
        v.put(Events.CALENDAR_ID, calendarId);
        v.put(Events.TITLE, title);
        v.put(Events.ALL_DAY, 0);
        v.put(Events.DESCRIPTION, description);
        v.put(Events.DTSTART, dtStart);
        v.put(Events.DTEND, dtEnd);
        v.put(Events.EVENT_TIMEZONE, timezone);
        return v;
    }

    public boolean isNew() {
        //noinspection RedundantIfStatement
        if (id > 0) {
            return false;
        }
        return true;
    }

    /**
     * {@link Parcelable#writeToParcel(Parcel, int)}
     */
    @Override
    public void writeToParcel(
        final android.os.Parcel dest,
        final int flags
    ) {
        dest.writeLong(id);
        dest.writeLong(calendarId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(dtStart);
        dest.writeLong(dtEnd);
        dest.writeString(timezone);
    }

    private EventsEntity(final Parcel in) {
        id = in.readLong();
        calendarId = in.readLong();
        title = in.readString();
        description = in.readString();
        dtStart = in.readLong();
        dtEnd = in.readLong();
        timezone = in.readString();
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
    public static final Parcelable.Creator<EventsEntity> CREATOR = new Parcelable.Creator<EventsEntity>() {
        public EventsEntity createFromParcel(final Parcel in) {
            return new EventsEntity(in);
        }

        public EventsEntity[] newArray(final int size) {
            return new EventsEntity[size];
        }
    };
}
