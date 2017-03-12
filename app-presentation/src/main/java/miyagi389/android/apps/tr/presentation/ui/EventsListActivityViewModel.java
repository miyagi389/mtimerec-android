package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import miyagi389.android.apps.tr.presentation.util.ZonedDateTimeUtils;

public class EventsListActivityViewModel extends BaseObservable implements Parcelable {

    private long fromDate = ZonedDateTimeUtils.getThisMonth().toInstant().toEpochMilli();

    private long toDate = ZonedDateTimeUtils.getEndOfThisMonth().toInstant().toEpochMilli();

    EventsListActivityViewModel() {
    }

    @Bindable
    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(final long fromDate) {
        this.fromDate = fromDate;
        notifyChange();
    }

    @Bindable
    public long getToDate() {
        return toDate;
    }

    public void setToDate(final long toDate) {
        this.toDate = toDate;
        notifyChange();
    }

    @NonNull
    public String formatFromToDate(@NonNull final Context context) {
        return DateUtils.formatDateRange(
            context,
            fromDate,
            toDate,
            DateUtils.FORMAT_SHOW_DATE
        );
    }

    /**
     * {@link Parcelable} constructor
     */
    private EventsListActivityViewModel(final Parcel in) {
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
     * {@link Parcelable.Creator}
     */
    public static final Parcelable.Creator<EventsListActivityViewModel> CREATOR = new Parcelable.Creator<EventsListActivityViewModel>() {
        public EventsListActivityViewModel createFromParcel(final Parcel in) {
            return new EventsListActivityViewModel(in);
        }

        public EventsListActivityViewModel[] newArray(final int size) {
            return new EventsListActivityViewModel[size];
        }
    };
}
