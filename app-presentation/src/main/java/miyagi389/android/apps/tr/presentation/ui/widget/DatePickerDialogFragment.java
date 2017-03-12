package miyagi389.android.apps.tr.presentation.ui.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import miyagi389.android.apps.tr.presentation.R;

public class DatePickerDialogFragment
    extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

    private final DatePickerDialogFragment self = this;

    private static final String EXTRA_YEAR = "EXTRA_YEAR";
    private static final String EXTRA_MONTH = "EXTRA_MONTH";
    private static final String EXTRA_DAY = "EXTRA_DAY";

    public interface Listener {

        void onDateSet(
            @NonNull DatePickerDialogFragment fragment,
            int requestCode,
            int year,
            int month,
            int day
        );
    }

    @NonNull
    public static DatePickerDialogFragment newInstance(
        @NonNull final Fragment targetFragment,
        final int requestCode,
        final int year,
        final int month,
        final int day
    ) {
        final DatePickerDialogFragment f = new DatePickerDialogFragment();
        f.setTargetFragment(targetFragment, requestCode);
        final Bundle args = new Bundle();
        args.putInt(EXTRA_YEAR, year);
        args.putInt(EXTRA_MONTH, month);
        args.putInt(EXTRA_DAY, day);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final int year = args.getInt(EXTRA_YEAR);
        final int month = args.getInt(EXTRA_MONTH);
        final int day = args.getInt(EXTRA_DAY);
        return new DatePickerDialog(getContext(), R.style.AppTheme_Dialog_Alert, self, year, month, day);
    }

    /**
     * {@link DatePickerDialog.OnDateSetListener#onDateSet(DatePicker, int, int, int)}
     */
    public void onDateSet(
        final DatePicker view,
        final int year,
        final int month,
        final int day
    ) {
        final Listener listener = getListener();
        if (listener == null) {
            return;
        }
        listener.onDateSet(self, getTargetRequestCode(), year, month, day);
    }

    @Nullable
    private Listener getListener() {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Listener) {
            return (Listener) targetFragment;
        }
        return null;
    }
}
