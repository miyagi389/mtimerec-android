package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.TemplateListFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.AlertDialogFragment;
import miyagi389.android.apps.tr.presentation.ui.widget.IconWithItemAdapter;
import rx.android.content.ContentObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.eventbus.RxEventBus;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TemplateListFragment
    extends BaseFragment
    implements
    TemplateListAdapter.Listener,
    AlertDialogFragment.ListAdapterDelegate,
    AlertDialogFragment.OnItemClickListener {

    private static final String STATE_SELECTED_TEMPLATE = "STATE_SELECTED_TEMPLATE";

    private final TemplateListFragment self = this;

    private TemplateListFragmentBinding binding;

    private TemplateListFragmentViewModel viewModel;

    private TemplateListAdapter adapter;

    private Template selectedTemplate;

    @Inject
    TemplateRepository templateRepository;

    @Inject
    RxEventBus eventBus;

    // Required empty public constructor
    public TemplateListFragment() {
        setHasOptionsMenu(true);
    }

    @NonNull
    /*package*/ static TemplateListFragment newInstance() {
        return new TemplateListFragment();
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
        return inflater.inflate(R.layout.template_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        self.viewModel = createViewModel();

        self.adapter = new TemplateListAdapter(getContext(), self);

        self.binding = TemplateListFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);

        self.binding.recyclerView.setHasFixedSize(true);
        self.binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        self.binding.recyclerView.setAdapter(self.adapter);
        // RecyclerView の notifyItemChanged() 時のちらつきを止める
        ((DefaultItemAnimator) self.binding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (savedInstanceState == null) {
            renderViewModel();
        }
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            self.selectedTemplate = (Template) savedInstanceState.getSerializable(STATE_SELECTED_TEMPLATE);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (self.selectedTemplate != null) {
            outState.putSerializable(STATE_SELECTED_TEMPLATE, selectedTemplate);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerObservable();
        requestLoadData();
    }

    private void registerObservable() {
        self.eventBus.observable(Template.Changed.class)
            .doOnSubscribe(() -> Timber.d("Subscribing subscription: Template"))
            .doOnUnsubscribe(() -> Timber.d("Unsubscribing subscription: Template"))
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                event -> {
                    requestLoadData();
                }
            );
        ContentObservable.fromContentObserver(getContext(), Calendars.CONTENT_URI, true)
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
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
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
        self.templateRepository.findAll()
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> {
                self.viewModel.clearItems();
                self.viewModel.setLoading(true);
            })
            .doOnUnsubscribe(() -> self.viewModel.setLoading(false))
            .subscribe(
                template -> {
                    self.viewModel.addItem(template);
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

    private TemplateListFragmentViewModel createViewModel() {
        return new TemplateListFragmentViewModel();
    }

    /**
     * {@link TemplateListAdapter.Listener#onItemClick(TemplateListAdapter, int)}
     */
    @Override
    public void onItemClick(
        @NonNull final TemplateListAdapter adapter,
        final int position
    ) {
        final Template item = adapter.getItem(position);
        showCalendar(item);
    }

    /**
     * {@link TemplateListAdapter.Listener#onMenuEditClick(TemplateListAdapter, int)}
     */
    @Override
    public void onMenuEditClick(
        @NonNull final TemplateListAdapter adapter,
        final int position
    ) {
        final Template template = adapter.getItem(position);
        final Intent intent = TemplateEditActivity.newIntent(
            getContext(),
            template
        );
        startActivity(intent);
    }

    /**
     * {@link TemplateListAdapter.Listener#onMenuEventsClick(TemplateListAdapter, int)}
     */
    @Override
    public void onMenuEventsClick(
        @NonNull final TemplateListAdapter adapter,
        final int position
    ) {
        final Template template = adapter.getItem(position);
        final Intent intent = EventsListActivity.newIntent(
            getContext(),
            template
        );
        startActivity(intent);
    }

    private void showCalendar(@NonNull final Template template) {
        self.selectedTemplate = template;
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext());
        builder.setTitle(template.getEventTitle());
        builder.setAdapter(self, self);
        builder.show(getFragmentManager(), builder.getClass().getName());
    }

    /**
     * {@link AlertDialogFragment.ListAdapterDelegate#getListAdapter(AlertDialogFragment)}
     */
    @NonNull
    @Override
    public ListAdapter getListAdapter(@NonNull final AlertDialogFragment dialog) {
        final IconWithItemAdapter.Item[] items = new IconWithItemAdapter.Item[]{
            new IconWithItemAdapter.Item(R.drawable.template_list_fragment_item_start, getString(R.string.template_list_fragment_item_start_text)),
            new IconWithItemAdapter.Item(R.drawable.template_list_fragment_item_end, getString(R.string.template_list_fragment_item_end_text)),
        };
        return new IconWithItemAdapter(dialog.getContext(), items);
    }

    /**
     * {@link AlertDialogFragment.OnItemClickListener#onItemClick(AlertDialogFragment, int)}
     */
    @Override
    public void onItemClick(
        @NonNull final AlertDialogFragment dialog,
        final int which
    ) {
        final ListAdapter adapter = getListAdapter(dialog);
        final IconWithItemAdapter.Item item = (IconWithItemAdapter.Item) adapter.getItem(which);
        switch (which) {
            case 0:
                startEvent();
                break;
            case 1:
                endEvent();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void startEvent() {
        if (selectedTemplate == null) {
            Timber.w("selectedTemplate is null.");
        }

        self.templateRepository.startEvent(selectedTemplate)
            .toObservable()
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                id -> {
                    Timber.d("startEvent() id=%d", id);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    Toast.makeText(getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    selectedTemplate = null;
                },
                () -> {
                    selectedTemplate = null;
                }
            );
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void endEvent() {
        if (selectedTemplate == null) {
            Timber.w("selectedTemplate is null.");
        }

        self.templateRepository.endEvent(selectedTemplate)
            .toObservable()
            .compose(self.bindUntilEvent(FragmentEvent.PAUSE))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                id -> {
                    Timber.d("endEvent() id=%d", id);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    Toast.makeText(getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    selectedTemplate = null;
                },
                () -> {
                    selectedTemplate = null;
                }
            );
    }
}