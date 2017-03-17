package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.model.EventsCount;
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
        @Nullable final EventsCount eventsCount,
        @NonNull final TemplateDetailFragmentViewModel destination
    ) {
        final boolean isEmpty = eventsCount == null;
        if (isEmpty) {
            destination.setEventsCount(0);
            destination.setEventsDtStart(TemplateDetailFragmentViewModel.DT_EMPTY);
            destination.setEventsDtEnd(TemplateDetailFragmentViewModel.DT_EMPTY);
        } else {
            destination.setEventsCount(eventsCount.getCount());
            destination.setEventsDtStart(eventsCount.getMinDtStart().getTime());
            destination.setEventsDtEnd(eventsCount.getMaxDtEnd().getTime());
        }
        destination.notifyChange();
    }
}
