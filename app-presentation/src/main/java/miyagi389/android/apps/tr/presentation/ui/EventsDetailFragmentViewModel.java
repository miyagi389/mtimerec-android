package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;

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

    public void setDtStart(final long dtStart) {
        this.dtStart = dtStart;
        notifyChange();
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
