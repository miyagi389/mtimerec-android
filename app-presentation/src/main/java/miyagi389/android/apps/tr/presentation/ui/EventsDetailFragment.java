package miyagi389.android.apps.tr.presentation.ui;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsDetailFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.AlertDialogFragment;
import rx.android.content.ContentObservable;
import timber.log.Timber;

public class EventsDetailFragment extends BaseFragment implements AlertDialogFragment.OnClickPositiveListener {

    public interface Listener {

        void onSaved(@NonNull EventsDetailFragment fragment);

        void onDeleted(@NonNull EventsDetailFragment fragment);
    }

    public static final String EXTRA_ID = "EXTRA_ID";

    private static final String REQUEST_TAG_DELETE = "REQUEST_TAG_DELETE";

    private static final int REQUEST_CODE_EVENTS_EDIT = 1;

    private static final String STATE_MODEL = "STATE_MODEL";

    private final EventsDetailFragment self = this;

    private EventsDetailFragmentBinding binding;

    private EventsDetailFragmentViewModel viewModel;

    private final EventsDetailFragmentViewModelDataMapper dataMapper = new EventsDetailFragmentViewModelDataMapper();

    @Inject
    EventsRepository eventsRepository;

    private Listener listener;

    // Required empty public constructor
    public EventsDetailFragment() {
    }

    @NonNull
    /*package*/ static EventsDetailFragment newInstance(
        final long id
    ) {
        final EventsDetailFragment f = new EventsDetailFragment();
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
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        self.binding = EventsDetailFragmentBinding.bind(getView());
        self.binding.setViewModel(self.viewModel);
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
        inflater.inflate(R.menu.events_detail_fragment, menu);
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

    private void delete() {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(
            getContext(),
            R.style.AppTheme_Dialog_Alert
        );
        builder.setMessage(
            getString(R.string.events_detail_fragment_delete_button_message, self.viewModel.getTitle())
        );
        builder.setPositiveButton(R.string.events_detail_fragment_delete_button_positive, self);
        builder.setNegativeButton(android.R.string.cancel);
        builder.show(getFragmentManager(), REQUEST_TAG_DELETE);
    }

    private void deleteInternal() {
        hideKeyboard(self.binding.getRoot());

        self.eventsRepository.deleteById(getArgumentsId())
            .compose(self.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(o -> self.viewModel.setLoading(true))
            .doOnSuccess(o -> self.viewModel.setLoading(false))
            .doOnError(throwable -> self.viewModel.setLoading(false))
            .subscribe(
                id -> {
                    //noinspection Convert2MethodRef
                    self.listener.onDeleted(self);
                },
                throwable -> {
                    Timber.e(throwable, throwable.getMessage());
                    showError(throwable.getMessage());
                }
            );
    }

    private void edit() {
        final Intent intent = EventsEditActivity.newIntent(
            getContext(),
            getArgumentsId()
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
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void onActivityResultEventsEdit(
        final int resultCode
    ) {
        switch (resultCode) {
            case EventsEditActivity.RESULT_SAVED:
                self.listener.onSaved(self);
                break;
            default:
                break;
        }
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
