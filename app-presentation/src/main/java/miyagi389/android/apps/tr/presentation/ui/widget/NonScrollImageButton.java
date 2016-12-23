package miyagi389.android.apps.tr.presentation.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * A custom {@link ImageButton} which prevents parent ScrollView scrolling when used as the
 * anchor for a {@link android.support.v7.widget.PopupMenu}
 *
 * @see <a href="http://stackoverflow.com/questions/29473977/popupmenu-click-causing-recyclerview-to-scroll" />
 */
public class NonScrollImageButton extends AppCompatImageButton {

    public NonScrollImageButton(
        @NonNull final Context context,
        @Nullable final AttributeSet attrs
    ) {
        super(context, attrs);
    }

    @Override
    public boolean requestRectangleOnScreen(
        @Nullable final Rect rectangle,
        final boolean immediate
    ) {
        return false;
    }
}
