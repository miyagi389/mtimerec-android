package miyagi389.android.apps.tr.data.provider.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import miyagi389.android.apps.tr.data.provider.entity.CalendarsEntity;
import miyagi389.android.apps.tr.data.provider.mapper.CalendarsMapper;
import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.domain.repository.CalendarsRepository;
import rx.android.content.ContentObservable;

public class CalendarsRepositoryImpl implements CalendarsRepository {

    private final Context context;

    private final CalendarsMapper mapper;

    @Inject
    CalendarsRepositoryImpl(@NonNull final Context context) {
        this.context = context;
        this.mapper = new CalendarsMapper();
    }

    @Override
    public Observable<Calendars> findWritableCalendar() {
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final String selection = CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
            + " >= "
            + CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR
            + " AND "
            + CalendarContract.Calendars.SYNC_EVENTS
            + " = 1";
        final String sortOrder = CalendarContract.Calendars.ACCOUNT_NAME + ", " + CalendarContract.Calendars.DEFAULT_SORT_ORDER;
        return ContentObservable.fromContentProvider(cr, uri, null, selection, null, sortOrder)
            .map(cursor -> mapper.transform(new CalendarsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Maybe<Calendars> findById(final long id) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, id);
        return ContentObservable.fromContentProvider(cr, uri, null, null, null, null)
            .map(cursor -> mapper.transform(new CalendarsEntity.CursorWrapper(cursor)))
            .firstElement();
    }
}
