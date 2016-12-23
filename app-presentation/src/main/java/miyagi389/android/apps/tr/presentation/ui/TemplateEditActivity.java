package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateEditActivityBinding;

public class TemplateEditActivity extends BaseActivity implements TemplateEditFragment.Listener {

    public static final String EXTRA_TEMPLATE = "EXTRA_TEMPLATE";

    private final TemplateEditActivity self = this;

    private TemplateEditActivityBinding binding;

    private TemplateEditFragment templateEditFragment;

    @NonNull
    public static Intent newIntent(
        @NonNull final Context context,
        @NonNull final Template template
    ) {
        final Intent intent = new Intent(context, TemplateEditActivity.class);
        intent.putExtra(EXTRA_TEMPLATE, template);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.template_edit_activity);

        self.binding.toolbar.setTitle(getTitle());
        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.templateEditFragment = (TemplateEditFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.templateEditFragment == null) {
            final Template template = getIntentTemplate();
            if (template != null) {
                self.templateEditFragment = TemplateEditFragment.newInstance(getIntentTemplate());
                replaceFragment(R.id.content_wrapper, self.templateEditFragment);
            }
        }
    }

    @Nullable
    private Template getIntentTemplate() {
        final Intent intent = getIntent();
        return intent == null ? null : (Template) intent.getSerializableExtra(EXTRA_TEMPLATE);
    }

    /**
     * {@link TemplateEditFragment.Listener#onSaved(TemplateEditFragment)}
     */
    @Override
    public void onSaved(@NonNull final TemplateEditFragment fragment) {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * {@link TemplateEditFragment.Listener#onDeleted(TemplateEditFragment)}
     */
    @Override
    public void onDeleted(@NonNull final TemplateEditFragment fragment) {
        setResult(RESULT_OK);
        finish();
    }
}
