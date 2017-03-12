package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class TemplateListFragmentViewModel extends BaseObservable {

    private final TemplateListFragmentViewModel self = this;

    private boolean loading;

    private List<Template> items = Collections.emptyList();

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

    public List<Template> getItems() {
        return self.items;
    }

    public void setItems(@NonNull final List<Template> items) {
        self.items = items;
        notifyPropertyChanged(BR.empty);
    }
}
