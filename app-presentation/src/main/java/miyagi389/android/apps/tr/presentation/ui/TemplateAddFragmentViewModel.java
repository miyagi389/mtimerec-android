package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import miyagi389.android.apps.tr.presentation.BR;

public class TemplateAddFragmentViewModel extends BaseObservable implements Parcelable {

    private String eventTitle;

    private long calendarId;

    private String calendarDisplayName;

    private boolean loading;

    TemplateAddFragmentViewModel() {
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

    void setCalendarDisplayName(final String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
        notifyPropertyChanged(BR.calendarDisplayName);
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
        return false;
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

    /**
     * {@link Parcelable} constructor
     */
    private TemplateAddFragmentViewModel(final Parcel in) {
        eventTitle = in.readString();
        calendarId = in.readLong();
        calendarDisplayName = in.readString();
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
        dest.writeString(eventTitle);
        dest.writeLong(calendarId);
        dest.writeString(calendarDisplayName);
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
     * {@link Parcelable.Creator}
     */
    public static final Parcelable.Creator<TemplateAddFragmentViewModel> CREATOR = new Parcelable.Creator<TemplateAddFragmentViewModel>() {
        public TemplateAddFragmentViewModel createFromParcel(final Parcel in) {
            return new TemplateAddFragmentViewModel(in);
        }

        public TemplateAddFragmentViewModel[] newArray(final int size) {
            return new TemplateAddFragmentViewModel[size];
        }
    };
}
