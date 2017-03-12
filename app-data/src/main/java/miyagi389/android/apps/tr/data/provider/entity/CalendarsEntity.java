package miyagi389.android.apps.tr.data.provider.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract.Calendars;
import android.support.annotation.NonNull;

/**
 * {@link android.provider.CalendarContract.Calendars} wrapper
 */
public class CalendarsEntity implements Parcelable {

    public static class CursorWrapper extends android.database.CursorWrapper {

        private final int mColumnIndexId;
        private final int mColumnIndexName;
        private final int mColumnIndexAccountName;
        private final int mColumnIndexAccountType;
        private final int mColumnIndexCalendarColor;
        private final int mColumnIndexCalendarDisplayName;
        private final int mColumnIndexCalendarAccessLevel;
        private final int mColumnIndexVisible;

        public CursorWrapper(@NonNull final Cursor cursor) {
            super(cursor);
            mColumnIndexId = getColumnIndex(Calendars._ID);
            mColumnIndexName = getColumnIndex(Calendars.NAME);
            mColumnIndexAccountName = getColumnIndex(Calendars.ACCOUNT_NAME);
            mColumnIndexAccountType = getColumnIndex(Calendars.ACCOUNT_TYPE);
            mColumnIndexCalendarColor = getColumnIndex(Calendars.CALENDAR_COLOR);
            mColumnIndexCalendarDisplayName = getColumnIndex(Calendars.CALENDAR_DISPLAY_NAME);
            mColumnIndexCalendarAccessLevel = getColumnIndex(Calendars.CALENDAR_ACCESS_LEVEL);
            mColumnIndexVisible = getColumnIndex(Calendars.VISIBLE);
        }

        @SuppressWarnings("unused")
        @NonNull
        public CalendarsEntity toEntity() {
            final CalendarsEntity o = new CalendarsEntity();
            if (mColumnIndexId != -1) {
                o.id = getId();
            }
            if (mColumnIndexName != -1) {
                o.name = getName();
            }
            if (mColumnIndexAccountName != -1) {
                o.accountName = getAccountName();
            }
            if (mColumnIndexAccountType != -1) {
                o.accountType = getAccountType();
            }
            if (mColumnIndexCalendarColor != -1) {
                o.calendarColor = getCalendarColor();
            }
            if (mColumnIndexCalendarDisplayName != -1) {
                o.calendarDisplayName = getCalendarDisplayName();
            }
            if (mColumnIndexCalendarAccessLevel != -1) {
                o.calendarAccessLevel = getCalendarAccessLevel();
            }
            if (mColumnIndexVisible != -1) {
                o.visible = getVisible();
            }
            return o;
        }

        public long getId() {
            return getLong(mColumnIndexId);
        }

        @NonNull
        public String getName() {
            return getString(mColumnIndexName);
        }

        @NonNull
        public String getAccountType() {
            return getString(mColumnIndexAccountType);
        }

        @NonNull
        public String getAccountName() {
            return getString(mColumnIndexAccountName);
        }

        public int getCalendarColor() {
            return getInt(mColumnIndexCalendarColor);
        }

        @NonNull
        public String getCalendarDisplayName() {
            return getString(mColumnIndexCalendarDisplayName);
        }

        public int getCalendarAccessLevel() {
            return getInt(mColumnIndexCalendarAccessLevel);
        }

        public boolean getVisible() {
            return getInt(mColumnIndexVisible) != 0;
        }
    }

    public long id;
    public String name;
    @SuppressWarnings("WeakerAccess")
    public String accountName;
    @SuppressWarnings("WeakerAccess")
    public String accountType;
    @SuppressWarnings("WeakerAccess")
    public int calendarColor;
    public String calendarDisplayName;
    @SuppressWarnings("WeakerAccess")
    public int calendarAccessLevel;
    public boolean visible;

    @SuppressWarnings("WeakerAccess")
    public CalendarsEntity() {
    }

    @Override
    public String toString() {
        return "CalendarsEntity{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", accountName='" + accountName + '\'' +
            ", accountType='" + accountType + '\'' +
            ", calendarColor='" + calendarColor + '\'' +
            ", calendarDisplayName='" + calendarDisplayName + '\'' +
            ", calendarAccessLevel=" + calendarAccessLevel +
            ", visible=" + visible +
            '}';
    }

    /**
     * {@link Parcelable} constructor
     */
    private CalendarsEntity(final Parcel in) {
        id = in.readLong();
        name = in.readString();
        accountName = in.readString();
        accountType = in.readString();
        calendarColor = in.readInt();
        calendarDisplayName = in.readString();
        calendarAccessLevel = in.readInt();
        visible = in.readInt() == 1;
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
        dest.writeString(name);
        dest.writeString(accountName);
        dest.writeString(accountType);
        dest.writeInt(calendarColor);
        dest.writeString(calendarDisplayName);
        dest.writeInt(calendarAccessLevel);
        dest.writeInt(visible ? 1 : 0);
    }

    /**
     * {@link Parcelable#describeContents()}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CalendarsEntity> CREATOR = new Parcelable.Creator<CalendarsEntity>() {
        public CalendarsEntity createFromParcel(final Parcel in) {
            return new CalendarsEntity(in);
        }

        public CalendarsEntity[] newArray(final int size) {
            return new CalendarsEntity[size];
        }
    };
}
