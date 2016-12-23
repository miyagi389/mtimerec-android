package miyagi389.android.apps.tr.domain.repository;

import miyagi389.android.apps.tr.domain.model.Events;
import rx.Observable;
import rx.Single;

public interface EventsRepository {

    Observable<Events> findByCalendarIdLast(
        long calendarId,
        String title
    );

    Observable<Events> findByCalendarId(
        long calendarId,
        String title,
        long fromDate,
        long toDate
    );

    Single<Events> findById(long id);

    Single<Long> insert(Events model);

    Single<Long> update(Events model);

    Single<Long> deleteById(long id);
}
