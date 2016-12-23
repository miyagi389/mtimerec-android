package miyagi389.android.apps.tr.presentation.di;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final ActivityModule self = this;

    final AppCompatActivity activity;

    public ActivityModule(@NonNull final AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    public Activity activity() {
        return self.activity;
    }
}
