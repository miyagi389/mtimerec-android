package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.CalendarsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateEditFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.AlertDialogFragment;
import miyagi389.android.apps.tr.presentation.ui.widget.ErrorLabelLayout;
import rx.Observable;
import rx.android.content.ContentObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TemplateEditFragment extends BaseFragment implements AlertDialogFragment.OnClickPositiveListener {

    public interface Listener {

        void onSaved(@NonNull TemplateEditFragment fragment);

        void onDeleted(@NonNull TemplateEditFragment fragment);
    }

    private static final int REQUEST_CODE_CALENDARS_CHOICE = 1;

    private static final String REQUEST_TAG_DELETE = "REQUEST_TAG_DELETE";

    public static final String EXTRA_TEMPLATE = "EXTRA_TEMPLATE";

    private static final String STATE_MODEL = "STATE_MODEL";

    private final TemplateEditFragment self = this;

    private TemplateEditFragmentBinding binding;

    private TemplateEditFragmentViewModel viewModel;

    private final TemplateEditFragmentViewModelDataMapper dataMapper = new TemplateEditFragmentViewModelDataMapper();

    @Inject
    CalendarsRepository calendarsRepository;

    @Inject
    TemplateRepository templateRepository;

    private Listener listener;

    public TemplateEditFragment() {
        // Required empty public constructor
    }

    @NonNull
    /*package*/ static TemplateEditFragment newInstance(
        @NonNull final Template template
    ) {
        final TemplateEditFragment f = new TemplateEditFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEMPLATE, template);
        f.setArguments(args);
        return f;
    }

    @Nullable
    private Template getArgumentsTemplate() {
        final Bundle args = getArguments();
        return args == null ? null : (Template) args.getSerializable(EXTRA_TEMPLATE);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        super.onCreate(savedInstanceState);
        getComponent().inject(self);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (!(context instanceof Listener)) {
            throw new ClassCastException(
                context.toString() + " must implement " + Listener.class.getSimpleName()
            );
        }
        self.listener = (Listener) context;
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.template_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            self.viewModel = new TemplateEditFragmentViewModel();
            final Template template = getArgumentsTemplate();
            if (template != null) {
                self.dataMapper.transform(getArgumentsTemplate(), self.viewModel);
            }
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = TemplateEditFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);

        self.binding.eventTitleErrorLabelLayout.setErrorPadding(0, 0, ErrorLabelLayout.DEFAULT_ERROR_LABEL_PADDING, 0);

        self.binding.calendarErrorLabelLayout.setErrorPadding(0, 0, ErrorLabelLayout.DEFAULT_ERROR_LABEL_PADDING, 0);

        self.binding.calendarButton.setOnClickListener(v -> choiceCalendar());

        renderViewModel();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MODEL, self.viewModel);
    }

    @Override
    public void onResume() {
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        super.onResume();
        registerObservable();
        requestLoadData();
    }

    private void registerObservable() {
        ContentObservable.fromContentObserver(getContext(), CalendarContract.Calendars.CONTENT_URI, true)
            .doOnSubscribe(() -> Timber.d("Subscribing subscription: Calendars"))
            .doOnUnsubscribe(() -> Timber.d("Unsubscribing subscription: Calendars"))
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                uri -> {
                    requestLoadData();
                }
            );
    }

    private void requestLoadData() {
        new RxPermissions(getActivity()).request(Manifest.permission.WRITE_CALENDAR)
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                granted -> {
                    if (granted) {
                        loadData();
                    }
                }
            );
    }

    @SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
    private void loadData() {
        final long calendarId = self.viewModel.getCalendarId();
        final boolean isEmptyCalendarId = (calendarId <= 0);
        final Observable<Calendars> calendarsObservable;
        if (isEmptyCalendarId) {
            calendarsObservable = self.calendarsRepository.findWritableCalendar();
        } else {
            calendarsObservable = self.calendarsRepository.findById(calendarId).toObservable();
        }

        calendarsObservable
            .limit(1)
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> self.viewModel.setLoading(true))
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                item -> {
                    self.viewModel.setCalendars(item);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    renderViewModel();
                    showError(throwable.getMessage());  // TODO error message
                },
                () -> {
                    renderViewModel();
                }
            );
    }

    @Override
    public void onCreateOptionsMenu(
        final Menu menu,
        final MenuInflater inflater
    ) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.template_edit_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                save();
                return true;
            case R.id.menu_delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderViewModel() {
        self.binding.setViewModel(self.viewModel);
        setHasOptionsMenu(!self.viewModel.isEmpty());
    }

    private void save() {
        hideKeyboard(self.binding.getRoot());

        if (!validate()) {
            return;
        }

        final Template template = new Template();
        self.dataMapper.transform(self.viewModel, template);

        //noinspection CodeBlock2Expr
        self.templateRepository.update(template)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> self.viewModel.setLoading(true))
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                id -> {
                    self.listener.onSaved(self);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());  // TODO error message
                }
            );
    }

    private boolean validate() {
        boolean result = true;
        //noinspection ConstantConditions
        result &= validateEventTitle();
        result &= validateCalendarId();
        return result;
    }

    private boolean validateEventTitle() {
        final String value = self.viewModel.getEventTitle();
        Timber.d("getEventTitle = %s", value);
        if (TextUtils.isEmpty(value)) {
            self.binding.eventTitleErrorLabelLayout.setError(getString(R.string.template_add_fragment_event_title_edit_error));
            return false;
        } else {
            self.binding.eventTitleErrorLabelLayout.clearError();
            return true;
        }
    }

    private boolean validateCalendarId() {
        final long value = self.viewModel.getCalendarId();
        if (value == 0) {
            self.binding.calendarErrorLabelLayout.setError(getString(R.string.template_add_fragment_calendar_button_error));
            return false;
        } else {
            self.binding.calendarErrorLabelLayout.clearError();
            return true;
        }
    }

    private void delete() {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(
            getContext(),
            R.style.AppTheme_Dialog_Alert
        );
        builder.setMessage(
            getString(R.string.template_edit_fragment_delete_button_message, self.viewModel.getEventTitle())
        );
        builder.setPositiveButton(R.string.template_edit_fragment_delete_button_positive, self);
        builder.setNegativeButton(android.R.string.cancel);
        builder.show(getFragmentManager(), REQUEST_TAG_DELETE);
    }

    private void deleteInternal() {
        hideKeyboard(self.binding.getRoot());

        if (!validate()) {
            return;
        }

        //noinspection CodeBlock2Expr
        self.templateRepository.deleteById(self.viewModel.getId())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> self.viewModel.setLoading(true))
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                id -> {
                    self.listener.onDeleted(self);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());  // TODO error message
                }
            );
    }

    private void choiceCalendar() {
        final Intent intent = CalendarsChoiceActivity.newIntent(
            getContext(),
            self.viewModel.getCalendarId()
        );
        startActivityForResult(intent, REQUEST_CODE_CALENDARS_CHOICE);
    }

    @Override
    public void onActivityResult(
        final int requestCode,
        final int resultCode,
        final Intent data
    ) {
        switch (requestCode) {
            case REQUEST_CODE_CALENDARS_CHOICE:
                onActivityResultCalendarsChoice(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void onActivityResultCalendarsChoice(
        final int resultCode,
        final Intent data
    ) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        final Calendars item = (Calendars) data.getSerializableExtra(CalendarsChoiceActivity.INTENT_CHOSEN_ITEM);
        self.viewModel.setCalendars(item);

        renderViewModel();
    }

    /**
     * {@link AlertDialogFragment.OnClickListener#onClickPositive(AlertDialogFragment)}
     */
    @Override
    public void onClickPositive(@NonNull final AlertDialogFragment dialog) {
        if (TextUtils.equals(dialog.getTag(), REQUEST_TAG_DELETE)) {
            deleteInternal();
        }
    }
}
