package miyagi389.android.apps.tr.data.provider.mapper;

import android.support.annotation.Nullable;

import miyagi389.android.apps.tr.data.provider.entity.CalendarsEntity;
import miyagi389.android.apps.tr.domain.model.Calendars;

public class CalendarsMapper {

    @Nullable
    public Calendars transform(@Nullable final CalendarsEntity.CursorWrapper cursor) {
        if (cursor == null) {
            return null;
        }
        final Calendars result = new Calendars();
        result.setId(cursor.getId());
        result.setName(cursor.getName());
        result.setAccountName(cursor.getAccountName());
        result.setAccountType(cursor.getAccountType());
        result.setCalendarColor(cursor.getCalendarColor());
        result.setCalendarDisplayName(cursor.getCalendarDisplayName());
        result.setCalendarAccessLevel(cursor.getCalendarAccessLevel());
        return result;
    }
}
