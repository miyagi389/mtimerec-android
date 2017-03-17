package miyagi389.android.apps.tr.domain.repository;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.model.EventsCount;

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

    Maybe<EventsCount> countByCalendarId(
        long calendarId,
        String title
    );

    Observable<Events> findByCalendarId(
        long calendarId,
        String title,
        long fromDate,
        long toDate,
        SortOrder sortOrder
    );

    Maybe<Events> findById(long id);

    Maybe<Long> insert(Events model);

    Maybe<Long> update(Events model);

    Maybe<Long> deleteById(long id);
}
