package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import miyagi389.android.apps.tr.domain.model.Events;

class EventsEditFragmentViewModelDataMapper {

    public void transform(
        @NonNull final EventsEditFragmentViewModel source,
        @NonNull final Events destination
    ) {
        destination.setId(source.getId());
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());
        destination.setDtStart(new Date(source.getDtStart()));
        destination.setDtEnd(new Date(source.getDtEnd()));
    }

    public void transform(
        @Nullable final Events source,
        @NonNull final EventsEditFragmentViewModel destination
    ) {
        destination.setId(source == null ? 0 : source.getId());
        destination.setTitle(source == null ? null : source.getTitle());
        destination.setDescription(source == null ? null : source.getDescription());
        destination.setDtStart(source == null ? 0 : source.getDtStart().getTime());
        destination.setDtEnd(source == null ? 0 : source.getDtEnd().getTime());
    }
}
