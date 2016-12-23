package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.CalendarsChoiceActivityBinding;
import timber.log.Timber;

public class CalendarsChoiceActivity extends BaseActivity implements CalendarsChoiceFragment.Listener {

    public static final String EXTRA_CHOSEN_ID = "EXTRA_CHOSEN_ID";

    public static final String INTENT_CHOSEN_ID = "INTENT_CHOSEN_ID";
    public static final String INTENT_CHOSEN_ITEM = "INTENT_CHOSEN_ITEM";

    private final CalendarsChoiceActivity self = this;

    private CalendarsChoiceActivityBinding binding;

    private CalendarsChoiceFragment calendarsChoiceFragment;

    @NonNull
    public static Intent newIntent(
        @NonNull final Context context,
        final long chosenId
    ) {
        final Intent intent = new Intent(context, CalendarsChoiceActivity.class);
        intent.putExtra(EXTRA_CHOSEN_ID, chosenId);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.calendars_choice_activity);

        self.binding.toolbar.setTitle(getTitle());
        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();

        self.calendarsChoiceFragment = (CalendarsChoiceFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.calendarsChoiceFragment == null) {
            self.calendarsChoiceFragment = CalendarsChoiceFragment.newInstance(getIntentChosenId());
            replaceFragment(R.id.content_wrapper, self.calendarsChoiceFragment);
        }
    }

    /**
     * {@link CalendarsChoiceFragment.Listener#onItemClick(CalendarsChoiceFragment, Calendars)}}
     */
    @Override
    public void onItemClick(
        @NonNull final CalendarsChoiceFragment fragment,
        @Nullable final Calendars item
    ) {
        Timber.d("onItemClick item=%s", item);
        // 選択したことがわかるように、 RadioButton の ON/OFF が反映されるまで待つ。
        runOnUiThreadDelayed(() -> {
            final Intent data = new Intent();
            if (item != null) {
                data.putExtra(INTENT_CHOSEN_ID, item.getId());
                data.putExtra(INTENT_CHOSEN_ITEM, item);
            }
            setResult(RESULT_OK, data);
            finish();
        }, 100);
    }

    private long getIntentChosenId() {
        final Intent intent = getIntent();
        return intent == null ? 0L : intent.getLongExtra(EXTRA_CHOSEN_ID, 0L);
    }
}
