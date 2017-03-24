package miyagi389.android.apps.tr.presentation;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class MainApplication extends AbstractMainApplication {

    @Override
    protected void initializeTimber() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(final StackTraceElement element) {
                return super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
    }

    @Override
    protected void initializeStetho() {
        Stetho.initializeWithDefaults(this);
    }
}
