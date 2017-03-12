package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class EventsEditFragmentViewModel extends BaseObservable implements Parcelable {

    private long id;

    private String title;

    private String description;

    private long dtStart;

    private long dtEnd;

    private boolean loading;

    EventsEditFragmentViewModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
        notifyPropertyChanged(BR.contentViewVisibility);
        notifyPropertyChanged(BR.loadingViewVisibility);
        notifyPropertyChanged(BR.emptyViewVisibility);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    public long getDtStart() {
        return dtStart;
    }

    public void setDtStart(final long dtStart) {
        this.dtStart = dtStart;
        notifyChange();
    }

    public long getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(final long dtEnd) {
        this.dtEnd = dtEnd;
        notifyChange();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(final boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.contentViewVisibility);
        notifyPropertyChanged(BR.loadingViewVisibility);
        notifyPropertyChanged(BR.emptyViewVisibility);
    }

    public boolean isEmpty() {
        return id == 0;
    }

    @Bindable
    public int getContentViewVisibility() {
        return (!isEmpty() && !isLoading()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getLoadingViewVisibility() {
        return isLoading() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getEmptyViewVisibility() {
        return (isEmpty() && !isLoading()) ? View.VISIBLE : View.GONE;
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

    public void setDtEndDate(
        final int year,
        final int month,
        final int day
    ) {
        final long value = getDtEndAsZonedDateTime()
            .withYear(year)
            .withMonth(month + 1)
            .withDayOfMonth(day)
            .toInstant()
            .toEpochMilli();
        setDtEnd(value);
    }

    public void setDtEndTime(
        final int hourOfDay,
        final int minute
    ) {
        final long value = getDtEndAsZonedDateTime()
            .withHour(hourOfDay)
            .withMinute(minute)
            .toInstant()
            .toEpochMilli();
        setDtEnd(value);
    }

    public void fixDtStartDate() {
        if (dtStart == 0) {
            return;
        }
        if (dtEnd == 0) {
            return;
        }
        final LocalDate dtStartLocalDate = getDtStartAsZonedDateTime().toLocalDate();
        final LocalDate dtEndLocalDate = getDtEndAsZonedDateTime().toLocalDate();
        if (dtStartLocalDate.compareTo(dtEndLocalDate) > 0) {
            final LocalTime dtEndLocalTime = getDtEndAsZonedDateTime().toLocalTime();
            final LocalDateTime dtEndLocalDateTime = LocalDateTime.of(dtStartLocalDate, dtEndLocalTime);
            setDtEnd(dtEndLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    public void fixDtStartTime() {
        if (dtStart == 0) {
            return;
        }
        if (dtEnd == 0) {
            return;
        }
        if (dtEnd < dtStart) {
            dtEnd = dtStart;
        }
    }

    public void fixDtEndDate() {
        if (dtStart == 0) {
            return;
        }
        if (dtEnd == 0) {
            return;
        }
        final LocalDate dtStartLocalDate = getDtStartAsZonedDateTime().toLocalDate();
        final LocalDate dtEndLocalDate = getDtEndAsZonedDateTime().toLocalDate();
        if (dtEndLocalDate.compareTo(dtStartLocalDate) < 0) {
            final LocalTime dtStartLocalTime = getDtStartAsZonedDateTime().toLocalTime();
            final LocalDateTime dtStartLocalDateTime = LocalDateTime.of(dtEndLocalDate, dtStartLocalTime);
            setDtStart(dtStartLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    public void fixDtEndTime() {
        if (dtStart == 0) {
            return;
        }
        if (dtEnd == 0) {
            return;
        }
        if (dtStart > dtEnd) {
            dtStart = dtEnd;
        }
    }

    @NonNull
    public String formatDtStartDate(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            dtStart,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
        );
    }

    @NonNull
    public String formatDtStartTime(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            dtStart,
            DateUtils.FORMAT_SHOW_TIME
        );
    }

    @NonNull
    public String formatDtEndDate(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            dtEnd,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
        );
    }

    @NonNull
    public String formatDtEndTime(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            dtEnd,
            DateUtils.FORMAT_SHOW_TIME
        );
    }

    /**
     * {@link Parcelable} constructor
     */
    private EventsEditFragmentViewModel(final Parcel in) {
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
    public static final Creator<EventsEditFragmentViewModel> CREATOR = new Creator<EventsEditFragmentViewModel>() {
        public EventsEditFragmentViewModel createFromParcel(final Parcel in) {
            return new EventsEditFragmentViewModel(in);
        }

        public EventsEditFragmentViewModel[] newArray(final int size) {
            return new EventsEditFragmentViewModel[size];
        }
    };
}
