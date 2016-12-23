package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import miyagi389.android.apps.tr.domain.model.Events;

class EventsDetailFragmentViewModelDataMapper {

    public void transform(
        @Nullable final Events source,
        @NonNull final EventsDetailFragmentViewModel destination
    ) {
        destination.setId(source == null ? 0 : source.getId());
        destination.setTitle(source == null ? null : source.getTitle());
        destination.setDescription(source == null ? null : source.getDescription());
        destination.setDtStart(source == null ? 0 : source.getDtStart().getTime());
        destination.setDtEnd(source == null ? 0 : source.getDtEnd().getTime());
    }
}
