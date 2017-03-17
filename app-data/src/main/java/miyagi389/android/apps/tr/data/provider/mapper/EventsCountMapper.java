package miyagi389.android.apps.tr.data.provider.mapper;

import android.support.annotation.Nullable;

import java.util.Date;

import miyagi389.android.apps.tr.data.provider.entity.EventsCountEntity;
import miyagi389.android.apps.tr.domain.model.EventsCount;

public class EventsCountMapper {

    @Nullable
    public EventsCount transform(@Nullable final EventsCountEntity.CursorWrapper cursor) {
        if (cursor == null) {
            return null;
        }
        final EventsCount result = new EventsCount();
        result.setCount(cursor.getTotalCount());
        result.setMinDtStart(new Date(cursor.getMinDtStart()));
        result.setMaxDtEnd(new Date(cursor.getMaxDtEnd()));
        return result;
    }
}
