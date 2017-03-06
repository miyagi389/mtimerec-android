package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
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

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.repository.CalendarsRepository;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateDetailFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.AlertDialogFragment;
import rx.android.content.ContentObservable;
import timber.log.Timber;

public class TemplateDetailFragment extends BaseFragment implements AlertDialogFragment.OnClickPositiveListener {

    public interface Listener {

        void onDeleted(@NonNull TemplateDetailFragment fragment);
    }

    private static final String REQUEST_TAG_DELETE = "REQUEST_TAG_DELETE";

    public static final String EXTRA_ID = "EXTRA_ID";

    private static final String STATE_MODEL = "STATE_MODEL";

    private final TemplateDetailFragment self = this;

    private TemplateDetailFragmentBinding binding;

    private TemplateDetailFragmentViewModel viewModel;

    private final TemplateDetailFragmentViewModelDataMapper dataMapper = new TemplateDetailFragmentViewModelDataMapper();

    @Inject
    CalendarsRepository calendarsRepository;

    @Inject
    EventsRepository eventsRepository;

    @Inject
    TemplateRepository templateRepository;

    private Listener listener;

    public TemplateDetailFragment() {
        // Required empty public constructor
    }

    @NonNull
    /*package*/ static TemplateDetailFragment newInstance(
        final long id
    ) {
        final TemplateDetailFragment f = new TemplateDetailFragment();
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
        return inflater.inflate(R.layout.template_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            self.viewModel = new TemplateDetailFragmentViewModel();
            self.viewModel.setId(getArgumentsId());
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = TemplateDetailFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);

        self.binding.eventsButton.setOnClickListener(v -> goEvents());

        renderViewModel();

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
    public void onResume() {
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        super.onResume();
        registerObservable();
        requestLoadData();
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
                    requestLoadData();
                }
            );
    }

    private void requestLoadData() {
        new RxPermissions(getActivity()).request(Manifest.permission.WRITE_CALENDAR)
            .compose(self.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                granted -> {
                    if (granted) {
                        loadDataTemplate();
                    }
                }
            );
    }

    private void loadDataTemplate() {
        self.templateRepository.findById(self.viewModel.getId())
            .toObservable()
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .subscribe(
                template -> {
                    //noinspection CodeBlock2Expr
                    self.dataMapper.transform(template, self.viewModel);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    renderViewModel();
                    showError(throwable.getMessage());
                },
                () -> {
                    //noinspection Convert2MethodRef
                    loadDataCalendar();
                }
            );
    }

    private void loadDataCalendar() {
        final long calendarId = self.viewModel.getCalendarId();
        final boolean isEmptyCalendarId = (calendarId <= 0);
        final Observable<Calendars> calendarsObservable;
        if (isEmptyCalendarId) {
            calendarsObservable = self.calendarsRepository.findWritableCalendar();
        } else {
            calendarsObservable = self.calendarsRepository.findById(calendarId).toObservable();
        }

        calendarsObservable
            .take(1)
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .subscribe(
                calendars -> {
                    //noinspection CodeBlock2Expr
                    self.dataMapper.transform(calendars, self.viewModel);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    renderViewModel();
                    showError(throwable.getMessage());
                },
                () -> {
                    //noinspection Convert2MethodRef
                    loadDataEvents();
                }
            );
    }

    private void loadDataEvents() {
        final long calendarId = self.viewModel.getCalendarId();
        final String eventTitle = self.viewModel.getEventTitle();
        self.eventsRepository.findByCalendarId(calendarId, eventTitle, EventsRepository.SortOrder.DT_START_ASCENDING)
            .compose(self.bindToLifecycle())
//            .onBackpressureBuffer()
            .observeOn(AndroidSchedulers.mainThread())
            .toList()
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnSuccess(o -> self.viewModel.setLoading(false))
            .doOnError(throwable -> self.viewModel.setLoading(false))
            .subscribe(
                events -> {
                    self.dataMapper.transform(events, self.viewModel);
                    renderViewModel();
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    renderViewModel();
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
        inflater.inflate(R.menu.template_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                delete();
                return true;
            case R.id.menu_edit:
                edit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderViewModel() {
        self.binding.setViewModel(self.viewModel);
        setHasOptionsMenu(!self.viewModel.isEmpty());
    }

    private void delete() {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(
            getContext(),
            R.style.AppTheme_Dialog_Alert
        );
        builder.setMessage(
            getString(R.string.template_detail_fragment_delete_button_message, self.viewModel.getEventTitle())
        );
        builder.setPositiveButton(R.string.template_detail_fragment_delete_button_positive, self);
        builder.setNegativeButton(android.R.string.cancel);
        builder.show(getFragmentManager(), REQUEST_TAG_DELETE);
    }

    private void deleteInternal() {
        hideKeyboard(self.binding.getRoot());

        self.templateRepository.deleteById(self.viewModel.getId())
            .compose(self.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnSuccess(o -> self.viewModel.setLoading(false))
            .doOnError(throwable -> self.viewModel.setLoading(false))
            .subscribe(
                id -> {
                    //noinspection CodeBlock2Expr
                    self.listener.onDeleted(self);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
                }
            );
    }

    private void edit() {
        final long id = getArgumentsId();
        final Intent intent = TemplateEditActivity.newIntent(getContext(), id);
        startActivity(intent);
    }

    private void goEvents() {
        final long id = getArgumentsId();
        final Intent intent = EventsListActivity.newIntent(getContext(), id);
        startActivity(intent);
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
