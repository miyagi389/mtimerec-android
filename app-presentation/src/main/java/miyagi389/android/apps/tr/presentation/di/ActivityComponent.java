package miyagi389.android.apps.tr.presentation.di;

import dagger.Subcomponent;
import miyagi389.android.apps.tr.presentation.di.scope.ActivityScope;
import miyagi389.android.apps.tr.presentation.ui.CalendarsChoiceActivity;
import miyagi389.android.apps.tr.presentation.ui.TemplateAddActivity;
import miyagi389.android.apps.tr.presentation.ui.TemplateEditActivity;
import miyagi389.android.apps.tr.presentation.ui.TemplateListActivity;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(CalendarsChoiceActivity activity);

    void inject(TemplateAddActivity activity);

    void inject(TemplateEditActivity activity);

    void inject(TemplateListActivity activity);

    FragmentComponent plus(FragmentModule module);
}
