package miyagi389.android.apps.tr.presentation.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import timber.log.Timber;

// fix: http://stackoverflow.com/questions/35653439/recycler-view-inconsistency-detected-invalid-view-holder-adapter-positionviewh
public class WrapContentLinearLayoutManager extends LinearLayoutManager {

    public WrapContentLinearLayoutManager(@NonNull final Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    public WrapContentLinearLayoutManager(
        @NonNull final Context context,
        final int orientation,
        final boolean reverseLayout
    ) {
        super(context, orientation, reverseLayout);
    }

    @SuppressWarnings("unused")
    public WrapContentLinearLayoutManager(
        @NonNull final Context context,
        @Nullable final AttributeSet attrs,
        final int defStyleAttr,
        final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(
        final RecyclerView.Recycler recycler,
        final RecyclerView.State state
    ) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (final IndexOutOfBoundsException e) {
            Timber.d("IndexOutOfBoundsException in RecyclerView happens");
        }
    }
}