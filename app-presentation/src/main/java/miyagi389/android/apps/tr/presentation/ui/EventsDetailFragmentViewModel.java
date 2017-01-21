package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class EventsDetailFragmentViewModel extends BaseObservable implements Parcelable {

    private long id;

    private String title;

    private String description;

    private long dtStart;

    private long dtEnd;

    private boolean loading;

    EventsDetailFragmentViewModel() {
    }

    @Bindable
    public boolean isEmpty() {
        return id == 0;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public void setTitle(final String title) {
        this.title = title;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    @Bindable
    public void setDescription(final String description) {
        this.description = description;
    }

    @Bindable
    public long getDtStart() {
        return dtStart;
    }

    @Bindable
    public void setDtStart(final long dtStart) {
        this.dtStart = dtStart;
        notifyChange();
    }

    @Bindable
    public long getDtEnd() {
        return dtEnd;
    }

    @Bindable
    public void setDtEnd(final long dtEnd) {
        this.dtEnd = dtEnd;
        notifyChange();
    }

    @Bindable
    public boolean isLoading() {
        return loading;
    }

    @Bindable
    public void setLoading(final boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
        notifyPropertyChanged(BR.empty);
    }

    @NonNull
    public ZonedDateTime getDtStartAsZonedDateTime() {
        final Instant instant = Instant.ofEpochMilli(this.dtStart);
        return instant.atZone(ZoneOffset.systemDefault());
    }

    public void setDtStartDate(
        final int year,
        final int month,
        final int day
    ) {
        final long value = getDtStartAsZonedDateTime()
            .withYear(year)
            .withMonth(month + 1)
            .withDayOfMonth(day)
            .toInstant()
            .toEpochMilli();
        setDtStart(value);
    }

    public void setDtStartTime(
        final int hourOfDay,
        final int minute
    ) {
        final long value = getDtStartAsZonedDateTime()
            .withHour(hourOfDay)
            .withMinute(minute)
            .toInstant()
            .toEpochMilli();
        setDtStart(value);
    }

    @NonNull
    public ZonedDateTime getDtEndAsZonedDateTime() {
        final Instant instant = Instant.ofEpochMilli(this.dtEnd);
        return instant.atZone(ZoneOffset.systemDefault());
    }

    @NonNull
    public String formatDtStart(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            dtStart,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_TIME
        );
    }

    @NonNull
    public String formatDtEnd(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            dtEnd,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_TIME
        );
    }

    /**
     * {@link Parcelable} constructor
     */
    private EventsDetailFragmentViewModel(final Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        dtStart = in.readLong();
        dtEnd = in.readLong();
        loading = in.readInt() == 1;
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
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(dtStart);
        dest.writeLong(dtEnd);
        dest.writeInt(loading ? 1 : 0);
    }

    /**
     * {@link Parcelable#describeContents()}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@link Creator}
     */
    public static final Creator<EventsDetailFragmentViewModel> CREATOR = new Creator<EventsDetailFragmentViewModel>() {
        public EventsDetailFragmentViewModel createFromParcel(final Parcel in) {
            return new EventsDetailFragmentViewModel(in);
        }

        public EventsDetailFragmentViewModel[] newArray(final int size) {
            return new EventsDetailFragmentViewModel[size];
        }
    };
}