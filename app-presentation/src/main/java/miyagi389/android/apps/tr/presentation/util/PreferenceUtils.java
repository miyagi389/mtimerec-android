package miyagi389.android.apps.tr.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.annotation.Nonnull;

import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.presentation.R;

public final class PreferenceUtils {

    private PreferenceUtils() {
    }

    public static final class UI {

        private UI() {
        }

        public static class EventsList {

            private EventsList() {
            }

            @Nonnull
            public static EventsRepository.SortOrder getSortOrder(@Nonnull final Context context) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                final String str = prefs.getString(context.getString(R.string.pref_ui_events_list_sort_order_key), null);
                final int value = IntegerUtils.tryParse(StringUtils.nullToEmpty(str), EventsRepository.SortOrder.DEFAULT.value);
                return EventsRepository.SortOrder.valueOfAsInt(value, EventsRepository.SortOrder.DEFAULT);
            }

            public static void setSortOrder(
                @Nonnull final Context context,
                @Nonnull final EventsRepository.SortOrder value
            ) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString(context.getString(R.string.pref_ui_events_list_sort_order_key), Integer.toString(value.value));
                editor.apply();
            }
        }
    }
}