package miyagi389.android.apps.tr.presentation.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    ActivityComponent plus(ActivityModule module);
}
