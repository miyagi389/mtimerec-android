package miyagi389.android.apps.tr.domain.repository;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import miyagi389.android.apps.tr.domain.model.Calendars;

public interface CalendarsRepository {

    // 書き込み可能な全カレンダー
    Observable<Calendars> findWritableCalendar();

    Maybe<Calendars> findById(long id);
}
