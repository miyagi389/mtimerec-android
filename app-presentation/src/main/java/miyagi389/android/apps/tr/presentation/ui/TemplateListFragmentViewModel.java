package miyagi389.android.apps.tr.presentation.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.presentation.BR;

@SuppressWarnings("WeakerAccess")
public class TemplateListFragmentViewModel extends BaseObservable {

    private final TemplateListFragmentViewModel self = this;

    private boolean loading;

    private final List<Template> items = new ArrayList<>();

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
        return self.items.isEmpty();
    }

    public List<Template> getItems() {
        return self.items;
    }

    public void addItem(final Template item) {
        self.items.add(item);
        notifyPropertyChanged(BR.empty);
    }

    public void clearItems() {
        self.items.clear();
        notifyPropertyChanged(BR.empty);
    }
}
