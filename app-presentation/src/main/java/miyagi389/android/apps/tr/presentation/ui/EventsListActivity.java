package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsListActivityBinding;

public class EventsListActivity extends BaseActivity implements EventsListFromToDateDialogFragment.Listener {

    public static final String EXTRA_TEMPLATE_ID = "EXTRA_TEMPLATE_ID";

    private static final int REQUEST_CODE_EVENTS_LIST_FROM_TO_DATE = 1;

    private final EventsListActivity self = this;

    private EventsListActivityBinding binding;

    private EventsListActivityViewModel viewModel;

    private EventsListFragment eventsListFragment;

    @NonNull
    public static Intent newIntent(
        @NonNull final Context context,
        final long templateId
    ) {
        final Intent intent = new Intent(context, EventsListActivity.class);
        intent.putExtra(EXTRA_TEMPLATE_ID, templateId);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.events_list_activity);

        self.viewModel = createViewModel();
        self.binding.setViewModel(self.viewModel);

        self.binding.periodButton.setOnClickListener(v -> choiceFromToDate());

        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.eventsListFragment = (EventsListFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.eventsListFragment == null) {
            final long templateId = getIntentTemplateId();
            if (templateId > 0) {
                self.eventsListFragment = EventsListFragment.newInstance(
                    templateId,
                    self.viewModel.getFromDate(),
                    self.viewModel.getToDate()
                );
                replaceFragment(R.id.content_wrapper, self.eventsListFragment);
            }
        }
    }

    private long getIntentTemplateId() {
        final Intent intent = getIntent();
        return intent == null ? 0L : intent.getLongExtra(EXTRA_TEMPLATE_ID, 0L);
    }

    private EventsListActivityViewModel createViewModel() {
        return new EventsListActivityViewModel();
    }

    private void choiceFromToDate() {
        final EventsListFromToDateDialogFragment f = EventsListFromToDateDialogFragment.newInstance(
            REQUEST_CODE_EVENTS_LIST_FROM_TO_DATE,
            self.viewModel.getFromDate(),
            self.viewModel.getToDate()
        );
        f.show(getSupportFragmentManager(), f.getClass().getName());
    }

    /**
     * {@link EventsListFromToDateDialogFragment.Listener#onDateSet(EventsListFromToDateDialogFragment, long, long)}
     */
    @Override
    public void onDateSet(
        @NonNull final EventsListFromToDateDialogFragment fragment,
        final long fromDate,
        final long toDate
    ) {
        self.viewModel.setFromDate(fromDate);
        self.viewModel.setToDate(toDate);

        self.eventsListFragment.setArgumentsFromDate(self.viewModel.getFromDate());
        self.eventsListFragment.setArgumentsToDate(self.viewModel.getToDate());
        self.eventsListFragment.requestLoadData();
    }

    /**
     * {@link EventsListFromToDateDialogFragment.Listener#onCancel(DialogInterface)}
     */
    @Override
    public void onCancel(
        @NonNull final EventsListFromToDateDialogFragment fragment,
        final int requestCode
    ) {
        // empty.
    }
}
