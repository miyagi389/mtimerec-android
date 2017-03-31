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

import com.annimon.stream.Optional;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.CalendarsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateEditFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.ErrorLabelLayout;
import rx.android.content.ContentObservable;
import timber.log.Timber;

public class TemplateEditFragment extends BaseFragment {

    public interface Listener {

        void onSaved(@NonNull TemplateEditFragment fragment);
    }

    private static final int REQUEST_CODE_CALENDARS_CHOICE = 1;

    public static final String EXTRA_ID = "EXTRA_ID";

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

    // Required empty public constructor
    public TemplateEditFragment() {
    }

    @NonNull
    /*package*/ static TemplateEditFragment newInstance(
        final long id
    ) {
        final TemplateEditFragment f = new TemplateEditFragment();
        final Bundle args = new Bundle();
        args.putLong(EXTRA_ID, id);
        f.setArguments(args);
        return f;
    }

    private long getArgumentsId() {
        final Bundle args = getArguments();
        return args == null ? 0L : args.getLong(EXTRA_ID);
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
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = TemplateEditFragmentBinding.bind(getView());
        self.binding.addOnPropertyChangedCallback(propertyChangedCallback);
        self.binding.setViewModel(self.viewModel);
        self.binding.getViewModel().addOnPropertyChangedCallback(propertyChangedCallback);

        self.binding.eventTitleErrorLabelLayout.setErrorPadding(0, 0, ErrorLabelLayout.DEFAULT_ERROR_LABEL_PADDING, 0);

        self.binding.calendarErrorLabelLayout.setErrorPadding(0, 0, ErrorLabelLayout.DEFAULT_ERROR_LABEL_PADDING, 0);

        self.binding.calendarButton.setOnClickListener(v -> choiceCalendar());

        if (savedInstanceState == null) {
            requestLoadData();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MODEL, self.viewModel);
    }

    @Override
    public void onStart() {
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        super.onStart();
        registerObservable();
    }

    private void registerObservable() {
        ContentObservable.fromContentObserver(getContext(), CalendarContract.Calendars.CONTENT_URI, true)
            .compose(self.bindToLifecycle())
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> Timber.d("Subscribe: Calendars"))
            .doOnTerminate(() -> Timber.d("Terminate: Calendars"))
            .subscribe(
                uri -> {
                    //noinspection CodeBlock2Expr
                    requestLoadData();
                }
            );
    }

    private void requestLoadData() {
        Optional.of(getActivity()).ifPresent(activity -> {
            new RxPermissions(activity).request(Manifest.permission.WRITE_CALENDAR)
                .compose(self.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    granted -> {
                        if (granted) {
                            loadData();
                        }
                    }
                );
        });
    }

    private void loadData() {
        //noinspection CodeBlock2Expr
        self.templateRepository.findById(getArgumentsId())
            .toObservable()
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .flatMap(template -> {
                self.dataMapper.transform(template, self.viewModel);

                final long calendarId = template.getCalendarId();
                final boolean isEmptyCalendarId = (calendarId <= 0);
                final Observable<Calendars> calendarsObservable;
                if (isEmptyCalendarId) {
                    calendarsObservable = self.calendarsRepository.findWritableCalendar();
                } else {
                    calendarsObservable = self.calendarsRepository.findById(calendarId).toObservable();
                }
                return calendarsObservable
                    .take(1);
            })
            .subscribe(
                calendars -> {
                    self.dataMapper.transform(calendars, self.viewModel);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        hideKeyboard(self.binding.getRoot());

        if (!validate()) {
            return;
        }

        final Template template = new Template();
        self.dataMapper.transform(self.viewModel, template);

        self.templateRepository.update(template)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnSuccess(o -> self.viewModel.setLoading(false))
            .doOnError(throwable -> self.viewModel.setLoading(false))
            .subscribe(
                id -> {
                    //noinspection Convert2MethodRef
                    self.listener.onSaved(self);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
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
            self.binding.eventTitleErrorLabelLayout.setError(getString(R.string.template_edit_fragment_event_title_edit_error));
            return false;
        } else {
            self.binding.eventTitleErrorLabelLayout.clearError();
            return true;
        }
    }

    private boolean validateCalendarId() {
        final long value = self.viewModel.getCalendarId();
        if (value == 0) {
            self.binding.calendarErrorLabelLayout.setError(getString(R.string.template_edit_fragment_calendar_button_error));
            return false;
        } else {
            self.binding.calendarErrorLabelLayout.clearError();
            return true;
        }
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

        final Calendars calendars = (Calendars) data.getSerializableExtra(CalendarsChoiceActivity.INTENT_CHOSEN_ITEM);
        self.dataMapper.transform(calendars, self.viewModel);
    }

    private final android.databinding.Observable.OnPropertyChangedCallback propertyChangedCallback = new android.databinding.Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(
            android.databinding.Observable sender,
            int propertyId
        ) {
            setHasOptionsMenu(!self.viewModel.isEmpty());
        }
    };
}
