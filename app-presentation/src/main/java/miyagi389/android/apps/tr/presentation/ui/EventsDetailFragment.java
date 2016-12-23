package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsDetailFragmentBinding;
import rx.android.content.ContentObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class EventsDetailFragment extends BaseFragment {

    public interface Listener {

        void onLoaded(
            @NonNull EventsDetailFragment fragment,
            @NonNull Events events
        );

        void onSaved(@NonNull EventsDetailFragment fragment);

        void onDeleted(@NonNull EventsDetailFragment fragment);
    }

    public static final String EXTRA_EVENTS_ID = "EXTRA_EVENTS_ID";

    private static final String STATE_MODEL = "STATE_MODEL";

    private static final int REQUEST_CODE_EVENTS_EDIT = 1;

    private final EventsDetailFragment self = this;

    private EventsDetailFragmentBinding binding;

    private EventsDetailFragmentViewModel viewModel;

    private final EventsDetailFragmentViewModelDataMapper dataMapper = new EventsDetailFragmentViewModelDataMapper();

    @Inject
    EventsRepository eventsRepository;

    private Listener listener;

    public EventsDetailFragment() {
        // Required empty public constructor
    }

    @NonNull
    /*package*/ static EventsDetailFragment newInstance(
        final long eventsId
    ) {
        final EventsDetailFragment f = new EventsDetailFragment();
        final Bundle args = new Bundle();
        args.putLong(EXTRA_EVENTS_ID, eventsId);
        f.setArguments(args);
        return f;
    }

    private long getArgumentsEventsId() {
        final Bundle args = getArguments();
        return args == null ? 0L : args.getLong(EXTRA_EVENTS_ID);
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
        return inflater.inflate(R.layout.events_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            self.viewModel = new EventsDetailFragmentViewModel();
            self.viewModel.setId(getArgumentsEventsId());
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = EventsDetailFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);

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
    }

    private void registerObservable() {
        final Uri contentObserverUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, getArgumentsEventsId());
        ContentObservable.fromContentObserver(getContext(), contentObserverUri, true)
            .doOnSubscribe(() -> Timber.d("Subscribing subscription: Events"))
            .doOnUnsubscribe(() -> Timber.d("Unsubscribing subscription: Events"))
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
        if (self.viewModel.isEmpty()) {
            return;
        }

        self.eventsRepository.findById(self.viewModel.getId())
            .toObservable()
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> self.viewModel.setLoading(true))
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                events -> {
                    self.dataMapper.transform(events, self.viewModel);
                    self.listener.onLoaded(self, events);
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
        inflater.inflate(R.menu.events_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
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

    private void edit() {
        final Intent intent = EventsEditActivity.newIntent(
            getContext(),
            self.viewModel.getId()
        );
        startActivityForResult(intent, REQUEST_CODE_EVENTS_EDIT);
    }

    @Override
    public void onActivityResult(
        final int requestCode,
        final int resultCode,
        final Intent data
    ) {
        switch (requestCode) {
            case REQUEST_CODE_EVENTS_EDIT:
                onActivityResultEventsEdit(resultCode);
                break;
            default:
                break;
        }
    }

    private void onActivityResultEventsEdit(
        final int resultCode
    ) {
        Timber.v("%s, resultCode=%d", new Throwable().getStackTrace()[0].getMethodName(), resultCode);
        switch (resultCode) {
            case EventsEditActivity.RESULT_SAVED:
                self.listener.onSaved(self);
                requestLoadData();
                break;
            case EventsEditActivity.RESULT_DELETED:
                self.listener.onDeleted(self);
                break;
            default:
                break;
        }
    }
}
