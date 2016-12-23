package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsListFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.AlertDialogFragment;
import rx.android.content.ContentObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.eventbus.RxEventBus;
import timber.log.Timber;

public class EventsListFragment
    extends BaseFragment
    implements
    AlertDialogFragment.OnClickPositiveListener,
    EventsListAdapter.Listener {

    public static final String EXTRA_TEMPLATE = "EXTRA_TEMPLATE";
    public static final String EXTRA_FROM_DATE = "EXTRA_FROM_DATE";
    public static final String EXTRA_TO_DATE = "EXTRA_TO_DATE";

    private static final String REQUEST_TAG_DELETE = "REQUEST_TAG_DELETE";
    private static final String EXTRA_DIALOG_ID = "EXTRA_DIALOG_ID";

    private final EventsListFragment self = this;

    private EventsListFragmentBinding binding;

    private EventsListFragmentViewModel viewModel;

    private EventsListAdapter adapter;

    @Inject
    EventsRepository eventsRepository;

    @Inject
    RxEventBus eventBus;

    // Required empty public constructor
    public EventsListFragment() {
    }

    @NonNull
    /*package*/ static EventsListFragment newInstance(
        @NonNull final Template template,
        final long fromDate,
        final long toDate
    ) {
        final EventsListFragment f = new EventsListFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEMPLATE, template);
        args.putLong(EXTRA_FROM_DATE, fromDate);
        args.putLong(EXTRA_TO_DATE, toDate);
        f.setArguments(args);
        return f;
    }

    @Nullable
    private Template getArgumentsTemplate() {
        final Bundle args = getArguments();
        return args == null ? null : (Template) args.getSerializable(EXTRA_TEMPLATE);
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
    public void onResume() {
        super.onResume();
        registerObservable();
        requestLoadData();
    }

    private void registerObservable() {
        ContentObservable.fromContentObserver(getContext(), android.provider.CalendarContract.Events.CONTENT_URI, true)
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

    public void requestLoadData() {
        new RxPermissions(getActivity()).request(Manifest.permission.READ_CALENDAR)
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
        final Template template = getArgumentsTemplate();
        if (template == null) {
            return;
        }

        final long fromDate = getArgumentsFromDate();

        final long toDate = getArgumentsToDate();

        self.eventsRepository.findByCalendarId(template.getCalendarId(), template.getEventTitle(), fromDate, toDate)
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> {
                self.viewModel.clearItems();
                self.viewModel.setLoading(true);
            })
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                events -> {
                    self.viewModel.addItem(events);
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

    private void renderViewModel() {
        self.adapter.clear();
        self.adapter.addAll(self.viewModel.getItems());
        self.adapter.notifyDataSetChanged();
    }

    private EventsListFragmentViewModel createViewModel() {
        return new EventsListFragmentViewModel();
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
     * {@link EventsListAdapter.Listener#onMenuEditClick(EventsListAdapter, int)}
     */
    @Override
    public void onMenuEditClick(
        @NonNull final EventsListAdapter adapter,
        final int position
    ) {
        final Intent intent = EventsEditActivity.newIntent(
            getContext(),
            adapter.getItemId(position)
        );
        startActivity(intent);
    }

    /**
     * {@link EventsListAdapter.Listener#onMenuDeleteClick(EventsListAdapter, int)}
     */
    @Override
    public void onMenuDeleteClick(
        @NonNull final EventsListAdapter adapter,
        final int position
    ) {
        final Events events = adapter.getItem(position);
        delete(events);
    }

    private void delete(@NonNull final Events events) {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(
            getContext(),
            R.style.AppTheme_Dialog_Alert
        );
        builder.setMessage(
            getString(R.string.events_list_fragment_delete_button_message, events.getTitle())
        );
        builder.setPositiveButton(R.string.events_list_fragment_delete_button_positive, self);
        builder.setNegativeButton(android.R.string.cancel);

        final DialogFragment f = builder.create();
        f.getArguments().putLong(EXTRA_DIALOG_ID, events.getId());
        f.show(getFragmentManager(), REQUEST_TAG_DELETE);
    }

    private void deleteInternal(final long eventsId) {
        //noinspection CodeBlock2Expr
        self.eventsRepository.deleteById(eventsId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> self.viewModel.setLoading(true))
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                id -> {
                    requestLoadData();
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());  // TODO error message
                }
            );
    }

    /**
     * {@link AlertDialogFragment.OnClickListener#onClickPositive(AlertDialogFragment)}
     */
    @Override
    public void onClickPositive(@NonNull final AlertDialogFragment dialog) {
        if (TextUtils.equals(dialog.getTag(), REQUEST_TAG_DELETE)) {
            final Bundle args = dialog.getArguments();
            final long id = dialog.getArguments().getLong(EXTRA_DIALOG_ID);
            deleteInternal(id);
        }
    }
}
