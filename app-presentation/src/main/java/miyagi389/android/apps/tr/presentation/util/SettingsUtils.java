package miyagi389.android.apps.tr.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.annotation.Nonnull;

import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import miyagi389.android.apps.tr.presentation.R;

public final class SettingsUtils {

    private SettingsUtils() {
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
                final String str = prefs.getString(context.getString(R.string.settings_ui_events_list_sort_order_key), null);
                final int value = IntegerUtils.tryParse(StringUtils.nullToEmpty(str), EventsRepository.SortOrder.DEFAULT.value);
                return EventsRepository.SortOrder.valueOfAsInt(value, EventsRepository.SortOrder.DEFAULT);
            }

            public static void setSortOrder(
                @Nonnull final Context context,
                @Nonnull final EventsRepository.SortOrder value
            ) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString(context.getString(R.string.settings_ui_events_list_sort_order_key), Integer.toString(value.value));
                editor.apply();
            }
        }

        public static class TemplateList {

            private TemplateList() {
            }

            @Nonnull
            public static TemplateRepository.SortOrder getSortOrder(@Nonnull final Context context) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                final String str = prefs.getString(context.getString(R.string.settings_ui_template_list_sort_order_key), null);
                final int value = IntegerUtils.tryParse(StringUtils.nullToEmpty(str), TemplateRepository.SortOrder.DEFAULT.value);
                return TemplateRepository.SortOrder.valueOfAsInt(value, TemplateRepository.SortOrder.DEFAULT);
            }

            public static void setSortOrder(
                @Nonnull final Context context,
                @Nonnull final TemplateRepository.SortOrder value
            ) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString(context.getString(R.string.settings_ui_template_list_sort_order_key), Integer.toString(value.value));
                editor.apply();
            }
        }
    }
}