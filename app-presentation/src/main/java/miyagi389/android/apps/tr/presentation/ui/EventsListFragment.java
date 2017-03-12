package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsListFragmentBinding;
import miyagi389.android.apps.tr.presentation.util.PreferenceUtils;
import rx.android.content.ContentObservable;
import rx.eventbus.RxEventBus;
import timber.log.Timber;

public class EventsListFragment extends BaseFragment implements EventsListAdapter.Listener {

    public static final String EXTRA_TEMPLATE_ID = "EXTRA_TEMPLATE_ID";
    public static final String EXTRA_FROM_DATE = "EXTRA_FROM_DATE";
    public static final String EXTRA_TO_DATE = "EXTRA_TO_DATE";

    private final EventsListFragment self = this;

    private EventsListFragmentBinding binding;

    private EventsListFragmentViewModel viewModel;

    private EventsListAdapter adapter;

    private EventsRepository.SortOrder sortOrder;

    @Inject
    EventsRepository eventsRepository;

    @Inject
    TemplateRepository templateRepository;

    @Inject
    RxEventBus eventBus;

    // Required empty public constructor
    public EventsListFragment() {
        setHasOptionsMenu(true);
    }

    @NonNull
    /*package*/ static EventsListFragment newInstance(
        final long templateId,
        final long fromDate,
        final long toDate
    ) {
        final EventsListFragment f = new EventsListFragment();
        final Bundle args = new Bundle();
        args.putLong(EXTRA_TEMPLATE_ID, templateId);
        args.putLong(EXTRA_FROM_DATE, fromDate);
        args.putLong(EXTRA_TO_DATE, toDate);
        f.setArguments(args);
        return f;
    }

    private long getArgumentsTemplateId() {
        final Bundle args = getArguments();
        return args == null ? 0L : args.getLong(EXTRA_TEMPLATE_ID);
    }

    private long getArgumentsFromDate() {
        final Bundle args = getArguments();
        return args == null ? -1 : args.getLong(EXTRA_FROM_DATE);
    }

    private long getArgumentsToDate() {
        final Bundle args = getArguments();
        return args == null ? -1 : args.getLong(EXTRA_TO_DATE);
    }

    public void setArgumentsFromDate(final long fromDate) {
        final Bundle args = getArguments();
        args.putLong(EXTRA_FROM_DATE, fromDate);
    }

    public void setArgumentsToDate(final long toDate) {
        final Bundle args = getArguments();
        args.putLong(EXTRA_TO_DATE, toDate);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        super.onCreate(savedInstanceState);
        getComponent().inject(self);
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.events_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        self.viewModel = createViewModel();

        self.adapter = new EventsListAdapter(getContext(), self);

        self.binding = EventsListFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);

        self.binding.recyclerView.setHasFixedSize(true);
        self.binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        self.binding.recyclerView.setAdapter(self.adapter);
        // RecyclerView の notifyItemChanged() 時のちらつきを止める
        ((DefaultItemAnimator) self.binding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        self.sortOrder = PreferenceUtils.UI.EventsList.getSortOrder(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        registerObservable();
        requestLoadData();
    }

    private void registerObservable() {
        ContentObservable.fromContentObserver(getContext(), android.provider.CalendarContract.Events.CONTENT_URI, true)
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

    public void requestLoadData() {
        new RxPermissions(getActivity()).request(Manifest.permission.READ_CALENDAR)
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
        self.templateRepository.findById(getArgumentsTemplateId())
            .toObservable()
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .subscribe(
                template -> {
                    //noinspection Convert2MethodRef
                    loadDataEvents(template);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    renderViewModel();
                    showError(throwable.getMessage());
                }
            );
    }

    private void loadDataEvents(@NonNull final Template template) {
        final long calendarId = template.getCalendarId();
        final String eventTitle = template.getEventTitle();
        final long fromDate = getArgumentsFromDate();
        final long toDate = getArgumentsToDate();
        self.eventsRepository.findByCalendarId(calendarId, eventTitle, fromDate, toDate, self.sortOrder)
            .compose(self.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> {
                self.viewModel.clearItems();
                self.viewModel.setLoading(true);
            })
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .subscribe(
                events -> {
                    //noinspection Convert2MethodRef
                    self.viewModel.addItem(events);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    renderViewModel();
                    showError(throwable.getMessage());
                },
                () -> {
                    //noinspection Convert2MethodRef
                    renderViewModel();
                }
            );
    }

    private void renderViewModel() {
        self.adapter.clear();
        self.adapter.addAll(self.viewModel.getItems());
        self.adapter.notifyDataSetChanged();
    }

    private EventsListFragmentViewModel createViewModel() {
        return new EventsListFragmentViewModel();
    }

    @Override
    public void onCreateOptionsMenu(
        final Menu menu,
        final MenuInflater inflater
    ) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.events_list_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        switch (self.sortOrder) {
            case DT_START_ASCENDING:
                menu.findItem(R.id.menu_sort_by_dt_start_ascending).setChecked(true);
                break;
            case DT_START_DESCENDING:
                menu.findItem(R.id.menu_sort_by_dt_start_descending).setChecked(true);
                break;
            default:
                super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_dt_start_ascending:
                self.sortOrder = EventsRepository.SortOrder.DT_START_ASCENDING;
                PreferenceUtils.UI.EventsList.setSortOrder(getContext(), self.sortOrder);
                requestLoadData();
                return true;
            case R.id.menu_sort_by_dt_start_descending:
                self.sortOrder = EventsRepository.SortOrder.DT_START_DESCENDING;
                PreferenceUtils.UI.EventsList.setSortOrder(getContext(), self.sortOrder);
                requestLoadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@link EventsListAdapter.Listener#onItemClick(EventsListAdapter, int)}
     */
    @Override
    public void onItemClick(
        @NonNull final EventsListAdapter adapter,
        final int position
    ) {
        final Intent intent = EventsDetailActivity.newIntent(
            getContext(),
            adapter.getItemId(position)
        );
        startActivity(intent);
    }

    /**
     * {@link EventsListAdapter.Listener#onMenuInfoClick(EventsListAdapter, int)}
     */
    @Override
    public void onMenuInfoClick(
        @NonNull final EventsListAdapter adapter,
        final int position
    ) {
        final Intent intent = EventsDetailActivity.newIntent(
            getContext(),
            adapter.getItemId(position)
        );
        startActivity(intent);
    }
}
