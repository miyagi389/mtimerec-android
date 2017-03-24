package miyagi389.android.apps.tr.presentation.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import java.util.HashMap;
import java.util.Map;

import miyagi389.android.apps.tr.presentation.BuildConfig;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.ui.WebViewActivity;
import miyagi389.android.apps.tr.presentation.util.ContextUtils;
import timber.log.Timber;

/**
 * 注意事項:
 * android.support.v7.preference.Preference のインスタンスにアクセスする際は、nullチェックを行うこと。
 * see: {@link SettingsActivity#onPreferenceStartScreen(PreferenceFragmentCompat, PreferenceScreen)}
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final SettingsFragment self = this;

    private final HashMap<String, PreferenceWrapper> preferenceWrappers = new HashMap<>();

    // Required empty public constructor
    public SettingsFragment() {
    }

    @NonNull
    /*package*/ static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(
        final Bundle bundle,
        final String rootKey
    ) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        initialize();
    }

    @Override
    public void onStart() {
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        super.onStart();

        final SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(self);
        updateSummary(sharedPreferences);
    }

    @Override
    public void onStop() {
        super.onStop();
        final SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(self);
    }

    /**
     * {@link SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(SharedPreferences, String)}
     */
    @Override
    public void onSharedPreferenceChanged(
        final SharedPreferences sharedPreferences,
        final String key
    ) {
        for (final Map.Entry<String, PreferenceWrapper> entry : self.preferenceWrappers.entrySet()) {
            final PreferenceWrapper p = entry.getValue();
            if (p.equalsKey(key)) {
                p.updateSummary();
            }
        }
    }

    private void initialize() {
        addPreferenceWrapper(new AboutAppVersionPreferenceWrapper());
        addPreferenceWrapper(new LegalOssPreferenceWrapper());

        for (final Map.Entry<String, PreferenceWrapper> entry : self.preferenceWrappers.entrySet()) {
            final PreferenceWrapper p = entry.getValue();
            p.initialize();
        }
    }

    private void addPreferenceWrapper(@NonNull final PreferenceWrapper p) {
        self.preferenceWrappers.put(p.getKey(), p);
    }

    private void updateSummary(
        @SuppressWarnings("unused") final SharedPreferences sharedPreferences
    ) {
        for (final Map.Entry<String, PreferenceWrapper> entry : self.preferenceWrappers.entrySet()) {
            final PreferenceWrapper p = entry.getValue();
            p.updateSummary();
        }
    }

    private abstract class PreferenceWrapper<T extends Preference> {

        private final T preference;

        PreferenceWrapper() {
            //noinspection unchecked
            this.preference = (T) findPreference(getKey());
        }

        @Nullable
        final T getPreference() {
            return preference;
        }

        final boolean equalsKey(final String key) {
            return getKey().equals(key);
        }

        @NonNull
        abstract String getKey();

        @SuppressWarnings("unused")
        void initialize() {
        }

        @SuppressWarnings("unused")
        void updateSummary() {
        }
    }


    private class LegalOssPreferenceWrapper extends PreferenceWrapper<Preference> {

        @NonNull
        @Override
        String getKey() {
            return getString(R.string.settings_legal_oss_key);
        }

        @Override
        void initialize() {
            final Preference p = getPreference();
            if (p != null) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClass(getContext(), WebViewActivity.class);
                intent.setData(Uri.parse("file:///android_asset/oss.html"));
                intent.putExtra(Intent.EXTRA_TITLE, p.getTitle());
                p.setIntent(intent);
            }
        }
    }

    private class AboutAppVersionPreferenceWrapper extends PreferenceWrapper<Preference> {

        @NonNull
        @Override
        String getKey() {
            return getString(R.string.settings_about_app_version_key);
        }

        @Override
        void updateSummary() {
            final Preference p = getPreference();
            if (p != null) {
                String s = ContextUtils.getAppVersionName(getContext());
                if (BuildConfig.DEBUG) {
                    s += " (" + ContextUtils.getAppVersionCode(getContext()) + ")";
                }
                p.setSummary(s);
            }
        }
    }
}
