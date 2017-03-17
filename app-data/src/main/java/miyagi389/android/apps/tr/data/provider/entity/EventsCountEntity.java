package miyagi389.android.apps.tr.data.provider.entity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import miyagi389.android.apps.tr.domain.model.EventsCount;

/**
 * {@link EventsCount} wrapper
 */
public class EventsCountEntity {

    /**
     * column strings
     */
    @SuppressWarnings("WeakerAccess")
    public interface Columns extends BaseColumns {

        /**
         * The time the event starts in UTC millis since epoch. Column name.
         * <P>Type: INTEGER (long; millis since epoch)</P>
         */
        String _MIN_DTSTART = "min_dtstart";

        /**
         * The time the event starts in UTC millis since epoch. Column name.
         * <P>Type: INTEGER (long; millis since epoch)</P>
         */
        String _MAX_DTEND = "max_dtend";
    }

    public static class CursorWrapper extends android.database.CursorWrapper {

        private final int mColumnIndexCount;
        private final int mColumnIndexMinDtStart;
        private final int mColumnIndexMaxDtEnd;

        public CursorWrapper(@NonNull final Cursor cursor) {
            super(cursor);
            mColumnIndexCount = getColumnIndex(Columns._COUNT);
            mColumnIndexMinDtStart = getColumnIndex(Columns._MIN_DTSTART);
            mColumnIndexMaxDtEnd = getColumnIndex(Columns._MAX_DTEND);
        }

        public int getTotalCount() {
            return getInt(mColumnIndexCount);
        }

        public long getMinDtStart() {
            return getLong(mColumnIndexMinDtStart);
        }

        public long getMaxDtEnd() {
            return getLong(mColumnIndexMaxDtEnd);
        }
    }

    public static final class Utils {

        private Utils() {
        }

        @Nullable
        public static CursorWrapper toCursorWrapperCalendarId(
            @NonNull final Context context,
            final long calendarId,
            @NonNull final String title
        ) {
            final Uri uri = Events.CONTENT_URI;
            final String[] projection = new String[]{
                "COUNT(*) AS " + Columns._COUNT,
                "MIN(" + Events.DTSTART + ") AS " + Columns._MIN_DTSTART,
                "MAX(" + Events.DTEND + ") AS " + Columns._MAX_DTEND,
            };
            final String selection = Events.CALENDAR_ID + " = ? AND " +
                Events.TITLE + " = ? AND " +
                Events.RRULE + " IS NULL AND " +
                Events.ALL_DAY + " = 0 AND " +
                Events.DELETED + " = 0";
            final String[] selectionArgs = new String[]{
                Long.toString(calendarId),
                title
            };
            final ContentResolver cr = context.getContentResolver();
            //noinspection MissingPermission Permission check は呼び出し元で行う。
            @SuppressLint("Recycle") final Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);
            if (cursor == null) {
                return null;
            }
            return new CursorWrapper(cursor);
        }
    }
}
