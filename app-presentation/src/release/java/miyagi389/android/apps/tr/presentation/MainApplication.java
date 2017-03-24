package miyagi389.android.apps.tr.presentation;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class MainApplication extends AbstractMainApplication {

    @Override
    protected void initializeTimber() {
        Timber.plant(new Timber.Tree() {
            @Override
            protected void log(
                final int priority,
                final String tag,
                final String message,
                final Throwable t
            ) {
                switch (priority) {
                    case Log.ERROR:
                        if (t == null) {
                            FirebaseCrash.report(new Exception(message));
                        } else {
                            FirebaseCrash.report(t);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        Timber.v("PROJECT_ROOT/google-services.json is required for release build. %d", R.string.google_crash_reporting_api_key);
    }

    @Override
    protected void initializeStetho() {
        // empty
    }
}
