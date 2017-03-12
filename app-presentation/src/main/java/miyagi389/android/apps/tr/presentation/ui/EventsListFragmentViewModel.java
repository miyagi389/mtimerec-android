package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Collections;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class EventsListFragmentViewModel extends BaseObservable {

    private List<Events> items = Collections.emptyList();

    private boolean loading;

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

    public List<Events> getItems() {
        return items;
    }

    public void setItems(@NonNull final List<Events> items) {
        this.items = items;
        notifyPropertyChanged(BR.contentViewVisibility);
        notifyPropertyChanged(BR.loadingViewVisibility);
        notifyPropertyChanged(BR.emptyViewVisibility);
    }
}
