package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsEditActivityBinding;

public class EventsEditActivity extends BaseActivity implements EventsEditFragment.Listener {

    public static final String EXTRA_EVENTS_ID = "EXTRA_EVENTS_ID";

    public static final int RESULT_SAVED = RESULT_FIRST_USER + 1;
    public static final int RESULT_DELETED = RESULT_FIRST_USER + 2;

    private final EventsEditActivity self = this;

    private EventsEditActivityBinding binding;

    private EventsEditFragment eventsEditFragment;

    @NonNull
    public static Intent newIntent(
        @NonNull final Context context,
        final long eventsId
    ) {
        final Intent intent = new Intent(context, EventsEditActivity.class);
        intent.putExtra(EXTRA_EVENTS_ID, eventsId);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.events_edit_activity);

        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.eventsEditFragment = (EventsEditFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.eventsEditFragment == null) {
            final long eventsId = getIntentEventsId();
            if (eventsId > 0) {
                self.eventsEditFragment = EventsEditFragment.newInstance(getIntentEventsId());
                replaceFragment(R.id.content_wrapper, self.eventsEditFragment);
            }
        }
    }

    private long getIntentEventsId() {
        final Intent intent = getIntent();
        return intent == null ? 0L : intent.getLongExtra(EXTRA_EVENTS_ID, 0L);
    }

    /**
     * {@link EventsEditFragment.Listener#onLoaded(EventsEditFragment, Events)}
     */
    @Override
    public void onLoaded(
        @NonNull final EventsEditFragment fragment,
        @NonNull final Events events
    ) {
        self.binding.toolbar.setTitle(events.getTitle());
    }

    /**
     * {@link EventsEditFragment.Listener#onSaved(EventsEditFragment)}
     */
    @Override
    public void onSaved(@NonNull final EventsEditFragment fragment) {
        setResult(RESULT_SAVED);
        finish();
    }

    /**
     * {@link EventsEditFragment.Listener#onDeleted(EventsEditFragment)}
     */
    @Override
    public void onDeleted(@NonNull final EventsEditFragment fragment) {
        setResult(RESULT_DELETED);
        finish();
    }
}
