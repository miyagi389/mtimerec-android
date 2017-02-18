package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.model.Template;

class TemplateEditFragmentViewModelDataMapper {

    public void transform(
        @NonNull final TemplateEditFragmentViewModel source,
        @NonNull final Template destination
    ) {
        destination.setId(source.getId());
        destination.setCalendarId(source.getCalendarId());
        destination.setEventTitle(source.getEventTitle());
    }

    public void transform(
        @NonNull final Template source,
        @NonNull final TemplateEditFragmentViewModel destination
    ) {
        destination.setId(source.getId());
        destination.setCalendarId(source.getCalendarId());
        destination.setEventTitle(source.getEventTitle());
    }

    public void transform(
        @NonNull final Calendars calendars,
        @NonNull final TemplateEditFragmentViewModel destination
    ) {
        destination.setCalendarId(calendars.getId());
        destination.setCalendarDisplayName(calendars.getCalendarDisplayName() + " (" + calendars.getAccountName() + ")");
    }
}
