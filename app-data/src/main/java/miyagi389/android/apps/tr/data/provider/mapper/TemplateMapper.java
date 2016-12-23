package miyagi389.android.apps.tr.data.provider.mapper;

import android.support.annotation.Nullable;

import miyagi389.android.apps.tr.data.provider.entity.TemplateEntity;
import miyagi389.android.apps.tr.domain.model.Template;

public class TemplateMapper {

    @Nullable
    public Template transform(@Nullable final TemplateEntity.CursorWrapper cursor) {
        if (cursor == null) {
            return null;
        }
        return new Template(
            cursor.getId(),
            cursor.getCalendarId(),
            cursor.getEventTitle()
        );
    }

    @Nullable
    public TemplateEntity transform(@Nullable final Template model) {
        if (model == null) {
            return null;
        }
        final TemplateEntity result = new TemplateEntity();
        result.id = model.getId();
        result.calendarId = model.getCalendarId();
        result.eventTitle = model.getEventTitle();
        return result;
    }
}
