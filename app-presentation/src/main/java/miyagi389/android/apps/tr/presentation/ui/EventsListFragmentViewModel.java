package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class EventsListFragmentViewModel extends BaseObservable {

    private final EventsListFragmentViewModel self = this;

    private boolean loading;

    private List<Events> items = Collections.emptyList();

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

    public void setItems(@NonNull final List<Events> items) {
        self.items = items;
        notifyPropertyChanged(BR.empty);
    }
}
