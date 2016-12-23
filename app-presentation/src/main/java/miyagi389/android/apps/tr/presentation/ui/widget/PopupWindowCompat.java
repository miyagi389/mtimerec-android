package miyagi389.android.apps.tr.presentation.ui.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import timber.log.Timber;

public final class PopupWindowCompat {

    public static void setForceShowIcon(@NonNull final PopupMenu popupMenu) {
        try {
            final Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (final Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    final Object menuPopupHelper = field.get(popupMenu);
                    final Class<?> classPopupHelper = Class.forName(menuPopupHelper
                        .getClass().getName());
                    final Method setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (final Throwable e) {
            Timber.e(e, e.getMessage());
        }
    }
}
