package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.repository.CalendarsRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.CalendarsChoiceFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.WrapContentLinearLayoutManager;
import rx.android.content.ContentObservable;
import timber.log.Timber;

public class CalendarsChoiceFragment
    extends BaseFragment
    implements CalendarsChoiceAdapter.Listener {

    public interface Listener {

        void onItemClick(
            @NonNull CalendarsChoiceFragment fragment,
            @Nullable Calendars item
        );
    }

    public static final String EXTRA_CHOSEN_ID = "EXTRA_CHOSEN_ID";

    private static final String STATE_MODEL = "STATE_MODEL";

    private final CalendarsChoiceFragment self = this;

    private CalendarsChoiceFragmentBinding binding;

    private CalendarsChoiceFragmentViewModel viewModel;

    private CalendarsChoiceAdapter adapter;

    @Inject
    CalendarsRepository calendarsRepository;

    private Listener listener;

    // Required empty public constructor
    public CalendarsChoiceFragment() {
    }

    @NonNull
    /*package*/ static CalendarsChoiceFragment newInstance(
        final long chosenId
    ) {
        final CalendarsChoiceFragment f = new CalendarsChoiceFragment();
        final Bundle args = new Bundle();
        args.putLong(EXTRA_CHOSEN_ID, chosenId);
        f.setArguments(args);
        return f;
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
    public void onDetach() {
        super.onDetach();
        self.listener = null;
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
        return inflater.inflate(R.layout.calendars_choice_fragment, container, false);
    }

    @Override
    public void onViewCreated(
        final View view,
        final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            self.viewModel = new CalendarsChoiceFragmentViewModel();
            self.viewModel.setChosenId(getArgumentsChosenId());
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = CalendarsChoiceFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);

        self.adapter = new CalendarsChoiceAdapter(getContext(), self);
        self.binding.recyclerView.setHasFixedSize(true);
        self.binding.recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        self.binding.recyclerView.setAdapter(self.adapter);
        // RecyclerView の notifyItemChanged() 時のちらつきを止める
        ((DefaultItemAnimator) self.binding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
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
        Timber.v(new Throwable().getStackTrace()[0].getMethodName());
        self.calendarsRepository.findWritableCalendar()
            .compose(self.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnTerminate(() -> self.viewModel.setLoading(false))
            .toList()
            .map(calendars -> {
                self.viewModel.setItems(new ArrayList<>(calendars));
                return DiffUtil.calculateDiff(
                    new CalendarsChoiceAdapterDiffUtilCallback(self.adapter.getItems(), self.viewModel.getItems())
                );
            })
            .subscribe(
                diffResult -> {
                    self.adapter.setItems(self.viewModel.getItems());
                    diffResult.dispatchUpdatesTo(self.adapter);
                    final int position = self.adapter.getPositionAtId(self.viewModel.getChosenId());
                    if (position != CalendarsChoiceAdapter.UNSELECTED_ITEM_POSITION) {
                        self.adapter.setItemChecked(position);
                    } else {
                        self.adapter.clearChoices();
                    }
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
                }
            );
    }

    private long getArgumentsChosenId() {
        final Bundle args = getArguments();
        return args == null ? 0L : args.getLong(EXTRA_CHOSEN_ID, 0L);
    }

    /**
     * {@link CalendarsChoiceAdapter.Listener#onItemClick(CalendarsChoiceAdapter, int)}
     */
    @Override
    public void onItemClick(
        @NonNull final CalendarsChoiceAdapter adapter,
        final int position
    ) {
        final Calendars item = adapter.getItem(position);
        if (item == null) {
            return;
        }
        self.viewModel.setChosenId(item.getId());
        self.listener.onItemClick(self, item);
    }
}
