package miyagi389.android.apps.tr.domain.repository;

import miyagi389.android.apps.tr.domain.model.Events;
import rx.Observable;
import rx.Single;

public interface EventsRepository {

    Observable<Events> findByCalendarIdLast(
        long calendarId,
        String title
    );

    enum SortOrder {
        DT_START_ASCENDING(1),
        DT_START_DESCENDING(2);

        public static final SortOrder DEFAULT = DT_START_ASCENDING;

        public final int value;

        SortOrder(final int value) {
            this.value = value;
        }

        public static SortOrder valueOfAsInt(
            final int value,
            final SortOrder defaultValue
        ) {
            for (final SortOrder o : values()) {
                if (o.value == value) {
                    return o;
                }
            }
            return defaultValue;
        }
    }


    Observable<Events> findByCalendarId(
        long calendarId,
        String title,
        long fromDate,
        long toDate,
        SortOrder sortOrder
    );

    Single<Events> findById(long id);

    Single<Long> insert(Events model);

    Single<Long> update(Events model);

    Single<Long> deleteById(long id);
}
