package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsDetailActivityBinding;

public class EventsDetailActivity extends BaseActivity implements EventsDetailFragment.Listener {

    public static final String EXTRA_ID = "EXTRA_ID";

    public static final int RESULT_SAVED = RESULT_FIRST_USER + 1;
    public static final int RESULT_DELETED = RESULT_FIRST_USER + 2;

    private final EventsDetailActivity self = this;

    private EventsDetailActivityBinding binding;

    private EventsDetailFragment eventsDetailFragment;

    @NonNull
    public static Intent newIntent(
        @NonNull final Context context,
        final long id
    ) {
        final Intent intent = new Intent(context, EventsDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.events_detail_activity);

        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.eventsDetailFragment = (EventsDetailFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.eventsDetailFragment == null) {
            final long id = getIntentId();
            if (id > 0) {
                self.eventsDetailFragment = EventsDetailFragment.newInstance(getIntentId());
                replaceFragment(R.id.content_wrapper, self.eventsDetailFragment);
            }
        }
    }

    private long getIntentId() {
        final Intent intent = getIntent();
        return intent == null ? 0L : intent.getLongExtra(EXTRA_ID, 0L);
    }

    /**
     * {@link EventsDetailFragment.Listener#onSaved(EventsDetailFragment)}
     */
    @Override
    public void onSaved(@NonNull final EventsDetailFragment fragment) {
        setResult(RESULT_SAVED);
    }

    /**
     * {@link EventsDetailFragment.Listener#onDeleted(EventsDetailFragment)}
     */
    @Override
    public void onDeleted(@NonNull final EventsDetailFragment fragment) {
        setResult(RESULT_DELETED);
        finish();
    }
}
