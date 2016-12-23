package miyagi389.android.apps.tr.presentation.di;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    private final FragmentModule self = this;

    private final Fragment fragment;

    public FragmentModule(@NonNull final Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public Context context() {
        return self.fragment.getContext();
    }

    @Provides
    public FragmentManager provideFragmentManager() {
        return self.fragment.getFragmentManager();
    }
}
