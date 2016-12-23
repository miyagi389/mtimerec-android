package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateAddActivityBinding;

public class TemplateAddActivity extends BaseActivity implements TemplateAddFragment.Listener {

    private final TemplateAddActivity self = this;

    private TemplateAddActivityBinding binding;

    private TemplateAddFragment templateAddFragment;

    @NonNull
    public static Intent newIntent(
        @NonNull final Context context
    ) {
        return new Intent(context, TemplateAddActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.template_add_activity);

        self.binding.toolbar.setTitle(getTitle());
        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.templateAddFragment = (TemplateAddFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.templateAddFragment == null) {
            self.templateAddFragment = TemplateAddFragment.newInstance();
            replaceFragment(R.id.content_wrapper, self.templateAddFragment);
        }
    }

    /**
     * {@link TemplateAddFragment.Listener#onSaved(TemplateAddFragment)}
     */
    @Override
    public void onSaved(@NonNull final TemplateAddFragment fragment) {
        setResult(RESULT_OK);
        finish();
    }
}
