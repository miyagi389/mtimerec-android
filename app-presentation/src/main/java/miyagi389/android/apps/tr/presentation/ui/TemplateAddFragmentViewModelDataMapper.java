package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;

import miyagi389.android.apps.tr.domain.model.Template;

class TemplateAddFragmentViewModelDataMapper {

    void transform(
        @NonNull final TemplateAddFragmentViewModel source,
        @NonNull final Template destination
    ) {
        destination.setEventTitle(source.getEventTitle());
        destination.setCalendarId(source.getCalendarId());
    }
}
