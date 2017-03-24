package miyagi389.android.apps.tr.presentation;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import miyagi389.android.apps.tr.presentation.di.ApplicationComponent;
import miyagi389.android.apps.tr.presentation.di.ApplicationModule;
import miyagi389.android.apps.tr.presentation.di.DaggerApplicationComponent;

public abstract class AbstractMainApplication extends Application {

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

    protected abstract void initializeTimber();

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

    protected abstract void initializeStetho();

    @NonNull
    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @NonNull
    public static RefWatcher getRefWatcher(@NonNull final Context context) {
        final AbstractMainApplication application = (AbstractMainApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
