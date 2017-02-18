package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;

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
}
