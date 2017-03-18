package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.threeten.bp.ZonedDateTime;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsEditFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.DatePickerDialogFragment;
import miyagi389.android.apps.tr.presentation.ui.widget.ErrorLabelLayout;
import miyagi389.android.apps.tr.presentation.ui.widget.TimePickerDialogFragment;
import rx.android.content.ContentObservable;
import timber.log.Timber;

public class EventsEditFragment
    extends BaseFragment
    implements
    DatePickerDialogFragment.Listener,
    TimePickerDialogFragment.Listener {

    public interface Listener {

        void onSaved(@NonNull EventsEditFragment fragment);
    }

    public static final String EXTRA_ID = "EXTRA_ID";

    private static final String STATE_MODEL = "STATE_MODEL";

    private static final int REQUEST_CODE_DT_START_DATE = 1;
    private static final int REQUEST_CODE_DT_START_TIME = 2;
    private static final int REQUEST_CODE_DT_END_DATE = 3;
    private static final int REQUEST_CODE_DT_END_TIME = 4;

    private final EventsEditFragment self = this;

    private EventsEditFragmentBinding binding;

    private EventsEditFragmentViewModel viewModel;

    private final EventsEditFragmentViewModelDataMapper dataMapper = new EventsEditFragmentViewModelDataMapper();

    @Inject
    EventsRepository eventsRepository;

    private Listener listener;

    // Required empty public constructor
    public EventsEditFragment() {
    }

    @NonNull
    /*package*/ static EventsEditFragment newInstance(
        final long id
    ) {
        final EventsEditFragment f = new EventsEditFragment();
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
        return inflater.inflate(R.layout.events_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            self.viewModel = new EventsEditFragmentViewModel();
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = EventsEditFragmentBinding.bind(getView());
        self.binding.dtStartDateButton.setOnClickListener(v -> {
            final ZonedDateTime o = self.viewModel.getDtStartAsZonedDateTime();
            final DialogFragment f = DatePickerDialogFragment.newInstance(
                self,
                REQUEST_CODE_DT_START_DATE,
                o.getYear(),
                o.getMonthValue() - 1,
                o.getDayOfMonth()
            );
            f.show(getFragmentManager(), f.getClass().getName());
        });
        self.binding.dtStartTimeButton.setOnClickListener(v -> {
            final ZonedDateTime o = self.viewModel.getDtStartAsZonedDateTime();
            final DialogFragment f = TimePickerDialogFragment.newInstance(
                self,
                REQUEST_CODE_DT_START_TIME,
                o.getHour(),
                o.getMinute()
            );
            f.show(getFragmentManager(), f.getClass().getName());
        });
        self.binding.dtEndDateButton.setOnClickListener(v -> {
            final ZonedDateTime o = self.viewModel.getDtEndAsZonedDateTime();
            final DialogFragment f = DatePickerDialogFragment.newInstance(
                self,
                REQUEST_CODE_DT_END_DATE,
                o.getYear(),
                o.getMonthValue() - 1,
                o.getDayOfMonth()
            );
            f.show(getFragmentManager(), f.getClass().getName());
        });
        self.binding.dtEndTimeButton.setOnClickListener(v -> {
            final ZonedDateTime o = self.viewModel.getDtEndAsZonedDateTime();
            final DialogFragment f = TimePickerDialogFragment.newInstance(
                self,
                REQUEST_CODE_DT_END_TIME,
                o.getHour(),
                o.getMinute()
            );
            f.show(getFragmentManager(), f.getClass().getName());
        });
        self.binding.setViewModel(self.viewModel);

        self.binding.descriptionErrorLabelLayout.setErrorPadding(0, 0, ErrorLabelLayout.DEFAULT_ERROR_LABEL_PADDING, 0);

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
    }

    private void registerObservable() {
        final Uri contentObserverUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, getArgumentsId());
        ContentObservable.fromContentObserver(getContext(), contentObserverUri, true)
            .compose(self.bindToLifecycle())
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> Timber.d("Subscribe: Events"))
            .doOnTerminate(() -> Timber.d("Terminate: Events"))
            .subscribe(
                uri -> {
                    //noinspection CodeBlock2Expr
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
                        loadData();
                    }
                }
            );
    }

    private void loadData() {
        //noinspection CodeBlock2Expr
        self.eventsRepository.findById(getArgumentsId())
            .toObservable()
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .subscribe(
                events -> {
                    self.dataMapper.transform(events, self.viewModel);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
                },
                () -> {
                    setHasOptionsMenu(!self.viewModel.isEmpty());
                }
            );
    }

    @Override
    public void onCreateOptionsMenu(
        final Menu menu,
        final MenuInflater inflater
    ) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.events_edit_fragment, menu);
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

        self.eventsRepository.findById(getArgumentsId())
            .toObservable()
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .subscribe(
                existEvents -> {
                    final Events updateEvents = new Events();
                    self.dataMapper.transform(self.viewModel, updateEvents);
                    self.dataMapper.transform(updateEvents, existEvents);
                    self.eventsRepository.update(existEvents)
                        .toObservable()
                        .observeOn(AndroidSchedulers.mainThread())
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
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
                }
            );
    }

    /**
     * {@link DatePickerDialogFragment.Listener#onDateSet(DatePickerDialogFragment, int, int, int, int)}
     */
    @Override
    public void onDateSet(
        @NonNull final DatePickerDialogFragment fragment,
        final int requestCode,
        final int year,
        final int month,
        final int day
    ) {
        switch (requestCode) {
            case REQUEST_CODE_DT_START_DATE:
                self.viewModel.setDtStartDate(year, month, day);
                self.viewModel.fixDtStartDate();
                break;
            case REQUEST_CODE_DT_END_DATE:
                self.viewModel.setDtEndDate(year, month, day);
                self.viewModel.fixDtEndDate();
                break;
            default:
                break;
        }
    }

    /**
     * {@link TimePickerDialogFragment.Listener#onTimeSet(TimePickerDialogFragment, int, int, int)}
     */
    @Override
    public void onTimeSet(
        @NonNull final TimePickerDialogFragment fragment,
        final int requestCode,
        final int hourOfDay,
        final int minute
    ) {
        switch (requestCode) {
            case REQUEST_CODE_DT_START_TIME:
                self.viewModel.setDtStartTime(hourOfDay, minute);
                self.viewModel.fixDtStartTime();
                break;
            case REQUEST_CODE_DT_END_TIME:
                self.viewModel.setDtEndTime(hourOfDay, minute);
                self.viewModel.fixDtEndTime();
                break;
            default:
                break;
        }
    }
}
