package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import miyagi389.android.apps.tr.presentation.BR;
import miyagi389.android.apps.tr.presentation.R;

public class TemplateDetailFragmentViewModel extends BaseObservable implements Parcelable {

    static final long DT_EMPTY = 0;

    private long id;

    private String eventTitle;

    private long calendarId;

    private String calendarDisplayName;

    private int eventsCount;

    private long eventsDtStart;

    private long eventsDtEnd;

    private boolean loading;

    TemplateDetailFragmentViewModel() {
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
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(final String eventTitle) {
        this.eventTitle = eventTitle;
        notifyPropertyChanged(BR.eventTitle);
    }

    long getCalendarId() {
        return calendarId;
    }

    void setCalendarId(final long calendarId) {
        this.calendarId = calendarId;
    }

    @Bindable
    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public void setCalendarDisplayName(final String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
        notifyPropertyChanged(BR.calendarDisplayName);
    }

    @Bindable
    public int getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(final int eventsCount) {
        this.eventsCount = eventsCount;
        notifyPropertyChanged(BR.eventsCount);
    }

    @Bindable
    public long getEventsDtStart() {
        return eventsDtStart;
    }

    public void setEventsDtStart(final long eventsDtStart) {
        this.eventsDtStart = eventsDtStart;
        notifyPropertyChanged(BR.eventsDtStart);
    }

    @Bindable
    public long getEventsDtEnd() {
        return eventsDtEnd;
    }

    public void setEventsDtEnd(final long eventsDtEnd) {
        this.eventsDtEnd = eventsDtEnd;
        notifyPropertyChanged(BR.eventsDtEnd);
    }

    @Bindable
    public boolean isLoading() {
        return loading;
    }

    public void setLoading(final boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
        notifyPropertyChanged(BR.empty);
    }

    @NonNull
    public String formatEventsCount(@NonNull final Context context) {
        return context.getString(R.string.template_detail_fragment_events_count_text, eventsCount);
    }

    @NonNull
    public String formatEventsDt(@NonNull final Context context) {
        final boolean isEmpty = (eventsDtStart == DT_EMPTY) && (eventsDtEnd == DT_EMPTY);
        if (isEmpty) {
            return " ";
        } else {
            return DateUtils.formatDateRange(
                context,
                eventsDtStart,
                eventsDtEnd,
                DateUtils.FORMAT_SHOW_DATE
            );
        }
    }

    /**
     * {@link Parcelable} constructor
     */
    private TemplateDetailFragmentViewModel(final Parcel in) {
        id = in.readLong();
        eventTitle = in.readString();
        calendarId = in.readLong();
        calendarDisplayName = in.readString();
        eventsCount = in.readInt();
        eventsDtStart = in.readLong();
        eventsDtEnd = in.readLong();
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
        dest.writeString(eventTitle);
        dest.writeLong(calendarId);
        dest.writeString(calendarDisplayName);
        dest.writeInt(eventsCount);
        dest.writeLong(eventsDtStart);
        dest.writeLong(eventsDtEnd);
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
    public static final Creator<TemplateDetailFragmentViewModel> CREATOR = new Creator<TemplateDetailFragmentViewModel>() {
        public TemplateDetailFragmentViewModel createFromParcel(final Parcel in) {
            return new TemplateDetailFragmentViewModel(in);
        }

        public TemplateDetailFragmentViewModel[] newArray(final int size) {
            return new TemplateDetailFragmentViewModel[size];
        }
    };
}
