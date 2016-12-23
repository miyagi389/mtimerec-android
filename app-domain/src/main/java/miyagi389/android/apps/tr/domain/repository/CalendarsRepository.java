package miyagi389.android.apps.tr.domain.repository;

import miyagi389.android.apps.tr.domain.model.Calendars;
import rx.Observable;
import rx.Single;

public interface CalendarsRepository {

    // 書き込み可能な全カレンダー
    Observable<Calendars> findWritableCalendar();

    Single<Calendars> findById(long id);
}
