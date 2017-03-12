package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class EventsListFragmentViewModel extends BaseObservable {

    private final EventsListFragmentViewModel self = this;

    private boolean loading;

    private final List<Events> items = new ArrayList<>();

    @Bindable
    public boolean isLoading() {
        return self.loading;
    }

    public void setLoading(final boolean loading) {
        self.loading = loading;
        notifyPropertyChanged(BR.loading);
        notifyPropertyChanged(BR.empty);
    }

    @Bindable
    public boolean isEmpty() {
        return self.items.isEmpty();
    }

    public List<Events> getItems() {
        return self.items;
    }

    public void addItem(final Events item) {
        self.items.add(item);
        notifyPropertyChanged(BR.empty);
    }

    public void clearItems() {
        self.items.clear();
        notifyPropertyChanged(BR.empty);
    }
}
