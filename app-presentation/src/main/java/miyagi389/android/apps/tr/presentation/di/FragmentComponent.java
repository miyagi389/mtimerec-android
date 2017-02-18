package miyagi389.android.apps.tr.presentation.di;

import dagger.Subcomponent;
import miyagi389.android.apps.tr.presentation.di.scope.FragmentScope;
import miyagi389.android.apps.tr.presentation.ui.CalendarsChoiceFragment;
import miyagi389.android.apps.tr.presentation.ui.EventsDetailFragment;
import miyagi389.android.apps.tr.presentation.ui.EventsEditFragment;
import miyagi389.android.apps.tr.presentation.ui.EventsListFragment;
import miyagi389.android.apps.tr.presentation.ui.TemplateAddFragment;
import miyagi389.android.apps.tr.presentation.ui.TemplateDetailFragment;
import miyagi389.android.apps.tr.presentation.ui.TemplateEditFragment;
import miyagi389.android.apps.tr.presentation.ui.TemplateListFragment;

@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(CalendarsChoiceFragment fragment);

    void inject(EventsDetailFragment fragment);

    void inject(EventsEditFragment fragment);

    void inject(EventsListFragment fragment);

    void inject(TemplateAddFragment fragment);

    void inject(TemplateDetailFragment fragment);

    void inject(TemplateEditFragment fragment);

    void inject(TemplateListFragment fragment);
}
