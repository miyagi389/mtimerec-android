package miyagi389.android.apps.tr.presentation.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.SettingsActivityBinding;

public class SettingsActivity extends RxAppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    private final SettingsActivity self = this;

    private SettingsActivityBinding binding;

    @NonNull
    public static Intent newIntent(@NonNull final Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity(savedInstanceState);
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.settings_activity);

        self.binding.toolbar.setTitle(getTitle());
        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            final Fragment fragment = SettingsFragment.newInstance();
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_wrapper, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceStartScreen(
        final PreferenceFragmentCompat preferenceFragmentCompat,
        final PreferenceScreen preferenceScreen
    ) {
        // <PreferenceCategory> のネストした画面遷移に対応する。
        // ネスト外の Preference は、null になるので null チェックすること。
        final Fragment f = SettingsFragment.newInstance();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        final Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        f.setArguments(args);
        ft.add(R.id.content_wrapper, f, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }
}
