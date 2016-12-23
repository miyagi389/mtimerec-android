package miyagi389.android.apps.tr.data.provider.mapper;

import android.support.annotation.Nullable;

import java.util.Date;

import miyagi389.android.apps.tr.data.provider.entity.EventsEntity;
import miyagi389.android.apps.tr.domain.model.Events;

public class EventsMapper {

    @Nullable
    public Events transform(@Nullable final EventsEntity.CursorWrapper cursor) {
        if (cursor == null) {
            return null;
        }
        final Events result = new Events();
        result.setId(cursor.getId());
        result.setCalendarId(cursor.getCalendarId());
        result.setTitle(cursor.getTitle());
        result.setDescription(cursor.getDescription(null));
        result.setDtStart(new Date(cursor.getDtStart()));
        result.setDtEnd(new Date(cursor.getDtEnd()));
        result.setTimezone(cursor.getTimezone());
        return result;
    }

    @Nullable
    public EventsEntity transform(@Nullable final Events model) {
        if (model == null) {
            return null;
        }
        final EventsEntity result = new EventsEntity();
        result.id = model.getId();
        result.calendarId = model.getCalendarId();
        result.title = model.getTitle();
        result.description = model.getDescription();
        if (model.getDtStart() != null) {
            result.dtStart = model.getDtStart().getTime();
        }
        if (model.getDtEnd() != null) {
            result.dtEnd = model.getDtEnd().getTime();
        }
        result.timezone = model.getTimezone();
        return result;
    }
}
