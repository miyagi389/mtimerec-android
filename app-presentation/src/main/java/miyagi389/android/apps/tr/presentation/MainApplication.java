package miyagi389.android.apps.tr.presentation;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import miyagi389.android.apps.tr.presentation.di.ApplicationComponent;
import miyagi389.android.apps.tr.presentation.di.ApplicationModule;
import miyagi389.android.apps.tr.presentation.di.DaggerApplicationComponent;
import timber.log.Timber;

public class MainApplication extends Application {

    private ApplicationComponent applicationComponent;

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeTimber();
        initializeAndroidThreeTen();
        initializeInjector();
        initializeLeakCanary();
        initializeStetho();
    }

    private void initializeTimber() {
        //noinspection StatementWithEmptyBody
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(final StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
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
        }
    }

    private void initializeAndroidThreeTen() {
        AndroidThreeTen.init(this);
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    private void initializeLeakCanary() {
        this.refWatcher = LeakCanary.install(this);
    }

    private void initializeStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    @NonNull
    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @NonNull
    public static RefWatcher getRefWatcher(@NonNull final Context context) {
        final MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
