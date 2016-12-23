package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

@SuppressWarnings("WeakerAccess")
public class EventsListFromToDateDialogFragmentViewModel extends BaseObservable implements Parcelable {

    private long fromDate;

    private long toDate;

    EventsListFromToDateDialogFragmentViewModel() {
    }

    @Bindable
    public long getFromDate() {
        return fromDate;
    }

    @Bindable
    public void setFromDate(final long fromDate) {
        this.fromDate = fromDate;
        fixFromDate();
        notifyChange();
    }

    @Bindable
    public long getToDate() {
        return toDate;
    }

    @Bindable
    public void setToDate(final long toDate) {
        this.toDate = toDate;
        fixToDate();
        notifyChange();
    }

    public ZonedDateTime getFromDateAsZonedDateTime() {
        final Instant instant = Instant.ofEpochMilli(this.fromDate);
        return instant.atZone(ZoneOffset.systemDefault());
    }

    public void setFromDate(
        final int year,
        final int month,
        final int day
    ) {
        final long value = getFromDateAsZonedDateTime()
            .withYear(year)
            .withMonth(month + 1)
            .withDayOfMonth(day)
            .toInstant()
            .toEpochMilli();
        setFromDate(value);
    }

    public ZonedDateTime getToDateAsZonedDateTime() {
        final Instant instant = Instant.ofEpochMilli(toDate);
        return instant.atZone(ZoneOffset.systemDefault());
    }

    public void setToDate(
        final int year,
        final int month,
        final int day
    ) {
        final long value = getToDateAsZonedDateTime()
            .withYear(year)
            .withMonth(month + 1)
            .withDayOfMonth(day)
            .toInstant()
            .toEpochMilli();
        setToDate(value);
    }

    private void fixFromDate() {
        if (fromDate == 0) {
            return;
        }
        if (toDate == 0) {
            return;
        }
        final LocalDate fromLocalDate = getFromDateAsZonedDateTime().toLocalDate();
        final LocalDate toLocalDate = getToDateAsZonedDateTime().toLocalDate();
        if (fromLocalDate.compareTo(toLocalDate) > 0) {
            final LocalTime toLocalTime = getToDateAsZonedDateTime().toLocalTime();
            final LocalDateTime toLocalDateTime = LocalDateTime.of(fromLocalDate, toLocalTime);
            setToDate(toLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    private void fixToDate() {
        if (fromDate == 0) {
            return;
        }
        if (toDate == 0) {
            return;
        }
        final LocalDate fromLocalDate = getFromDateAsZonedDateTime().toLocalDate();
        final LocalDate toLocalDate = getToDateAsZonedDateTime().toLocalDate();
        if (toLocalDate.compareTo(fromLocalDate) < 0) {
            final LocalTime fromLocalTime = getFromDateAsZonedDateTime().toLocalTime();
            final LocalDateTime fromLocalDateTime = LocalDateTime.of(toLocalDate, fromLocalTime);
            setFromDate(fromLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    @NonNull
    public String formatFromDate(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            fromDate,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
        );
    }

    @NonNull
    public String formatToDate(@NonNull final Context context) {
        return DateUtils.formatDateTime(
            context,
            toDate,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
        );
    }

    /**
     * {@link Parcelable} constructor
     */
    private EventsListFromToDateDialogFragmentViewModel(final Parcel in) {
        fromDate = in.readLong();
        toDate = in.readLong();
    }

    /**
     * {@link Parcelable#writeToParcel(Parcel, int)}
     */
    @Override
    public void writeToParcel(
        final Parcel dest,
        final int flags
    ) {
        dest.writeLong(fromDate);
        dest.writeLong(toDate);
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
    public static final Creator<EventsListFromToDateDialogFragmentViewModel> CREATOR = new Creator<EventsListFromToDateDialogFragmentViewModel>() {
        public EventsListFromToDateDialogFragmentViewModel createFromParcel(final Parcel in) {
            return new EventsListFromToDateDialogFragmentViewModel(in);
        }

        public EventsListFromToDateDialogFragmentViewModel[] newArray(final int size) {
            return new EventsListFromToDateDialogFragmentViewModel[size];
        }
    };
}
