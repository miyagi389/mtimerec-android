package miyagi389.android.apps.tr.presentation.util;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.YearMonth;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

public final class ZonedDateTimeUtils {

    private ZonedDateTimeUtils() {
    }

    /**
     * @return 今月の1日 00:00:00。1970 年 1 月 1 日 00:00:00 GMT からのミリ秒数。
     */
    @NonNull
    public static ZonedDateTime getThisMonth() {
        final LocalDateTime now = LocalDateTime.now();
        YearMonth ym = YearMonth.from(now);
        final LocalDateTime atStartOfDay = ym.atDay(1).atStartOfDay();
        return ZonedDateTime.of(atStartOfDay, ZoneId.systemDefault());
    }

    /**
     * @return 今月の末日 23:59:59。1970 年 1 月 1 日 00:00:00 GMT からのミリ秒数。
     */
    @NonNull
    public static ZonedDateTime getEndOfThisMonth() {
        final LocalDateTime now = LocalDateTime.now();
        YearMonth ym = YearMonth.from(now);
        final LocalDateTime atEndOfMonth = ym.atEndOfMonth().atTime(23, 59, 59);
        return ZonedDateTime.of(atEndOfMonth, ZoneId.systemDefault());
    }
}
