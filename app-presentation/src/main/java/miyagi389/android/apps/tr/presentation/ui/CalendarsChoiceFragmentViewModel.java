package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.presentation.BR;

public class CalendarsChoiceFragmentViewModel extends BaseObservable implements Parcelable {

    private long chosenId;

    private ArrayList<Calendars> items = new ArrayList<>();

    private boolean loading;

    CalendarsChoiceFragmentViewModel() {
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
        return items.isEmpty();
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

    public long getChosenId() {
        return chosenId;
    }

    public void setChosenId(final long chosenId) {
        this.chosenId = chosenId;
    }

    public List<Calendars> getItems() {
        return items;
    }

    public void setItems(@NonNull final ArrayList<Calendars> items) {
        this.items = items;
        notifyPropertyChanged(BR.contentViewVisibility);
        notifyPropertyChanged(BR.loadingViewVisibility);
        notifyPropertyChanged(BR.emptyViewVisibility);
    }

    /**
     * {@link Parcelable} constructor
     */
    private CalendarsChoiceFragmentViewModel(final Parcel in) {
        chosenId = in.readLong();
        items.addAll(in.readArrayList(Calendars.class.getClassLoader()));
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
        dest.writeLong(chosenId);
        dest.writeList(items);
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
    public static final Parcelable.Creator<CalendarsChoiceFragmentViewModel> CREATOR = new Parcelable.Creator<CalendarsChoiceFragmentViewModel>() {
        @Override
        public CalendarsChoiceFragmentViewModel createFromParcel(final Parcel in) {
            return new CalendarsChoiceFragmentViewModel(in);
        }

        @Override
        public CalendarsChoiceFragmentViewModel[] newArray(final int size) {
            return new CalendarsChoiceFragmentViewModel[size];
        }
    };
}
