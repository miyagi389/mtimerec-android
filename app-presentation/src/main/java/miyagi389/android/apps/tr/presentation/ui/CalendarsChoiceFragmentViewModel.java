package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class CalendarsChoiceFragmentViewModel extends BaseObservable implements Parcelable {

    private final CalendarsChoiceFragmentViewModel self = this;

    private long chosenId;

    private final ArrayList<Calendars> entities = new ArrayList<>();

    private boolean loading;

    CalendarsChoiceFragmentViewModel() {
    }

    @Bindable
    public boolean isLoading() {
        return self.loading;
    }

    @Bindable
    public void setLoading(final boolean loading) {
        self.loading = loading;
        notifyPropertyChanged(BR.loading);
        notifyPropertyChanged(BR.empty);
    }

    @Bindable
    public boolean isEmpty() {
        return self.entities.isEmpty();
    }

    public long getChosenId() {
        return self.chosenId;
    }

    public void setChosenId(final long chosenId) {
        self.chosenId = chosenId;
    }

    public List<Calendars> getEntities() {
        return self.entities;
    }

    public void addEntities(final List<Calendars> items) {
        self.entities.addAll(items);
    }

    public void clearEntities() {
        self.entities.clear();
        notifyPropertyChanged(BR.empty);
    }

    /**
     * {@link Parcelable} constructor
     */
    private CalendarsChoiceFragmentViewModel(final Parcel in) {
        chosenId = in.readLong();
        //noinspection unchecked
        entities.addAll(in.readArrayList(Calendars.class.getClassLoader()));
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
        dest.writeList(entities);
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
        public CalendarsChoiceFragmentViewModel createFromParcel(final Parcel in) {
            return new CalendarsChoiceFragmentViewModel(in);
        }

        public CalendarsChoiceFragmentViewModel[] newArray(final int size) {
            return new CalendarsChoiceFragmentViewModel[size];
        }
    };
}
