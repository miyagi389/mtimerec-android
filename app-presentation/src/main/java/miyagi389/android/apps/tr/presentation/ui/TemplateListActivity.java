package miyagi389.android.apps.tr.presentation.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateListActivityBinding;
import miyagi389.android.apps.tr.presentation.ui.settings.SettingsActivity;

public class TemplateListActivity extends BaseActivity {

    private final TemplateListActivity self = this;

    private TemplateListActivityBinding binding;

    private TemplateListFragment templateListFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.template_list_activity);

        self.binding.toolbar.setTitle(R.string.template_list_activity_title);
        setSupportActionBar(self.binding.toolbar);

        self.binding.fab.setOnClickListener(view -> add());
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.templateListFragment = (TemplateListFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.templateListFragment == null) {
            self.templateListFragment = TemplateListFragment.newInstance();
            replaceFragment(R.id.content_wrapper, self.templateListFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.template_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                goSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void add() {
        final Intent intent = TemplateAddActivity.newIntent(self);
        startActivity(intent);
    }

    private void goSettings() {
        final Intent intent = SettingsActivity.newIntent(self);
        startActivity(intent);
    }
}
