package miyagi389.android.apps.tr.presentation.ui.widget;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.TimePicker;

import miyagi389.android.apps.tr.presentation.R;

public class TimePickerDialogFragment
    extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

    private final TimePickerDialogFragment self = this;

    private static final String EXTRA_HOUR_OF_DAY = "EXTRA_HOUR_OF_DAY";
    private static final String EXTRA_MINUTE = "EXTRA_MINUTE";

    public interface Listener {

        void onTimeSet(
            @NonNull TimePickerDialogFragment fragment,
            int requestCode,
            int hourOfDay,
            int minute
        );
    }

    @NonNull
    public static TimePickerDialogFragment newInstance(
        @NonNull final Fragment targetFragment,
        final int requestCode,
        final int hourOfDay,
        final int minute
    ) {
        final TimePickerDialogFragment f = new TimePickerDialogFragment();
        f.setTargetFragment(targetFragment, requestCode);
        final Bundle args = new Bundle();
        args.putInt(EXTRA_HOUR_OF_DAY, hourOfDay);
        args.putInt(EXTRA_MINUTE, minute);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final int hourOfDay = args.getInt(EXTRA_HOUR_OF_DAY);
        final int minute = args.getInt(EXTRA_MINUTE);
        return new TimePickerDialog(getContext(), R.style.AppTheme_Dialog_Alert, self, hourOfDay, minute, true);
    }

    /**
     * {@link TimePickerDialog.OnTimeSetListener#onTimeSet(TimePicker, int, int)} )}
     */
    @Override
    public void onTimeSet(
        final TimePicker view,
        final int hourOfDay,
        final int minute
    ) {
        final Listener listener = getListener();
        if (listener == null) {
            return;
        }
        listener.onTimeSet(this, getTargetRequestCode(), hourOfDay, minute);
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
