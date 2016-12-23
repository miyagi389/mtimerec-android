package miyagi389.android.apps.tr.presentation.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import org.threeten.bp.ZonedDateTime;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.EventsListFromToDateDialogFragmentBinding;
import miyagi389.android.apps.tr.presentation.ui.widget.DatePickerDialogFragment;

public class EventsListFromToDateDialogFragment extends BaseDialogFragment implements DatePickerDialogFragment.Listener {

    public interface Listener {

        void onDateSet(
            @NonNull EventsListFromToDateDialogFragment fragment,
            long fromDate,
            long toDate
        );

        void onCancel(
            @NonNull EventsListFromToDateDialogFragment fragment,
            int requestCode
        );
    }

    public static final String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";
    public static final String EXTRA_FROM_DATE = "EXTRA_FROM_DATE";
    public static final String EXTRA_TO_DATE = "EXTRA_TO_DATE";

    private static final String STATE_MODEL = "STATE_MODEL";

    private static final int REQUEST_CODE_FROM_DATE = 1;
    private static final int REQUEST_CODE_TO_DATE = 2;

    private final EventsListFromToDateDialogFragment self = this;

    private EventsListFromToDateDialogFragmentBinding binding;

    private EventsListFromToDateDialogFragmentViewModel viewModel;

    @NonNull
    /*package*/ static EventsListFromToDateDialogFragment newInstance(
        @NonNull final Activity targetActivity,
        final int requestCode,
        final long fromDate,
        final long toDate
    ) {
        final EventsListFromToDateDialogFragment f = new EventsListFromToDateDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(EXTRA_REQUEST_CODE, requestCode);
        args.putLong(EXTRA_FROM_DATE, fromDate);
        args.putLong(EXTRA_TO_DATE, toDate);
        f.setArguments(args);
        return f;
    }

    @NonNull
    /*package*/ static EventsListFromToDateDialogFragment newInstance(
        @NonNull final Fragment targetFragment,
        final int requestCode,
        final long fromDate,
        final long toDate
    ) {
        final EventsListFromToDateDialogFragment f = new EventsListFromToDateDialogFragment();
        f.setTargetFragment(targetFragment, requestCode);
        final Bundle args = new Bundle();
        args.putLong(EXTRA_FROM_DATE, fromDate);
        args.putLong(EXTRA_TO_DATE, toDate);
        f.setArguments(args);
        return f;
    }

    private int getArgumentsRequestCode() {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Listener) {
            return getTargetRequestCode();
        }
        final Activity targetActivity = getActivity();
        if (targetActivity instanceof Listener) {
            final Bundle args = getArguments();
            return args == null ? 0 : args.getInt(EXTRA_REQUEST_CODE, 0);
        }
        return 0;
    }

    private long getArgumentsFromDate() {
        final Bundle args = getArguments();
        return args == null ? 0L : args.getLong(EXTRA_FROM_DATE, 0L);
    }

    private long getArgumentsToDate() {
        final Bundle args = getArguments();
        return args == null ? 0L : args.getLong(EXTRA_TO_DATE, 0L);
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            self.viewModel = new EventsListFromToDateDialogFragmentViewModel();
            self.viewModel.setFromDate(getArgumentsFromDate());
            self.viewModel.setToDate(getArgumentsToDate());
        } else {
            self.viewModel = savedInstanceState.getParcelable(STATE_MODEL);
        }

        final Context context = getContext();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alert);
        final AlertDialog dialog = builder.create();

        final LayoutInflater inflater = dialog.getLayoutInflater();
        final View view = inflater.inflate(R.layout.events_list_from_to_date_dialog_fragment, null);

        self.binding = EventsListFromToDateDialogFragmentBinding.bind(view);
        self.binding.fromDateButton.setOnClickListener(v -> {
            final ZonedDateTime o = self.viewModel.getFromDateAsZonedDateTime();
            final DialogFragment f = DatePickerDialogFragment.newInstance(
                self,
                REQUEST_CODE_FROM_DATE,
                o.getYear(),
                o.getMonthValue() - 1,
                o.getDayOfMonth()
            );
            f.show(getFragmentManager(), f.getClass().getName());
        });
        self.binding.toDateButton.setOnClickListener(v -> {
            final ZonedDateTime o = self.viewModel.getToDateAsZonedDateTime();
            final DialogFragment f = DatePickerDialogFragment.newInstance(
                self,
                REQUEST_CODE_TO_DATE,
                o.getYear(),
                o.getMonthValue() - 1,
                o.getDayOfMonth()
            );
            f.show(getFragmentManager(), f.getClass().getName());
        });
        self.binding.setViewModel(self.viewModel);

        dialog.setView(view);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MODEL, self.viewModel);
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        final Listener listener = getListener();
        if (listener != null) {
            listener.onCancel(self, getArgumentsRequestCode());
        }
    }

    @Nullable
    private Listener getListener() {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Listener) {
            return (Listener) targetFragment;
        }
        final Activity targetActivity = getActivity();
        if (targetActivity instanceof Listener) {
            return (Listener) targetActivity;
        }
        return null;
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
            case REQUEST_CODE_FROM_DATE: {
                self.viewModel.setFromDate(year, month, day);
                final Listener listener = getListener();
                if (listener != null) {
                    listener.onDateSet(self, self.viewModel.getFromDate(), self.viewModel.getToDate());
                }
                break;
            }
            case REQUEST_CODE_TO_DATE: {
                self.viewModel.setToDate(year, month, day);
                final Listener listener = getListener();
                if (listener != null) {
                    listener.onDateSet(self, self.viewModel.getFromDate(), self.viewModel.getToDate());
                }
                break;
            }
            default:
                break;
        }
    }
}
