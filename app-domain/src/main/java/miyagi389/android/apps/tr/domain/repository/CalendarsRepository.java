package miyagi389.android.apps.tr.domain.repository;

import io.reactivex.Observable;
import io.reactivex.Single;
import miyagi389.android.apps.tr.domain.model.Calendars;

public interface CalendarsRepository {

    // 書き込み可能な全カレンダー
    Observable<Calendars> findWritableCalendar();

    Single<Calendars> findById(long id);
}
