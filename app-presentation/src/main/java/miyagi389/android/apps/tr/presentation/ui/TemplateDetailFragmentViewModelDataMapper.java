package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.model.Template;

class TemplateDetailFragmentViewModelDataMapper {

    public void transform(
        @NonNull final Template source,
        @NonNull final TemplateDetailFragmentViewModel destination
    ) {
        destination.setId(source.getId());
        destination.setCalendarId(source.getCalendarId());
        destination.setEventTitle(source.getEventTitle());
    }

    public void transform(
        @NonNull final Calendars calendars,
        @NonNull final TemplateDetailFragmentViewModel destination
    ) {
        destination.setCalendarId(calendars.getId());
        destination.setCalendarDisplayName(calendars.getCalendarDisplayName() + " (" + calendars.getAccountName() + ")");
    }

    public void transform(
        @Nullable final List<Events> events,
        @NonNull final TemplateDetailFragmentViewModel destination
    ) {
        final boolean isEmpty = events == null || events.isEmpty();
        if (isEmpty) {
            destination.setEventsCount(0);
            destination.setEventsDtStart(TemplateDetailFragmentViewModel.DT_EMPTY);
            destination.setEventsDtEnd(TemplateDetailFragmentViewModel.DT_EMPTY);
        } else {
            destination.setEventsCount(events.size());
            final Events first = events.get(0);
            final Events last = events.get(events.size() - 1);
            destination.setEventsDtStart(first.getDtStart().getTime());
            destination.setEventsDtEnd(last.getDtEnd().getTime());
        }
    }
}
