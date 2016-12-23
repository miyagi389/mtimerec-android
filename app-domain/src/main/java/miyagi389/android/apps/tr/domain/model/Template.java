package miyagi389.android.apps.tr.domain.model;

import java.io.Serializable;
import java.util.Date;

import rx.eventbus.RxEvent;

/**
 * テンプレート
 */
public class Template implements Serializable {

    /**
     * 追加 or 変更 or 削除時に発生するイベント
     */
    public static class Changed extends RxEvent {
    }

    /**
     * 追加時に発生するイベント
     */
    public static class Created extends Changed {

        /**
         * 追加した id.
         */
        public final long id;

        public Created(final long id) {
            this.id = id;
        }
    }

    /**
     * 更新時に発生するイベント
     */
    public static class Updated extends Changed {

        /**
         * 更新した id.
         */
        public final long id;

        public Updated(final long id) {
            this.id = id;
        }
    }

    /**
     * 削除時に発生するイベント
     */
    public static class Deleted extends Changed {

        /**
         * 削除した id.
         */
        public final long id;

        public Deleted(final long id) {
            this.id = id;
        }
    }

    /**
     * id
     */
    private long id;

    /**
     * カレンダーID
     */
    private long calendarId;

    /**
     * イベントタイトル
     */
    private String eventTitle;

    /**
     * イベント開始日時
     */
    private Date dtStart;

    /**
     * イベント終了日時
     */
    private Date dtEnd;

    public Template() {
    }

    public Template(
        final long id,
        final long calendarId,
        final String eventTitle
    ) {
        this.id = id;
        this.calendarId = calendarId;
        this.eventTitle = eventTitle;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(final long calendarId) {
        this.calendarId = calendarId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(final String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getDtStart() {
        return dtStart;
    }

    public void setDtStart(final Date dtStart) {
        this.dtStart = dtStart;
    }

    public Date getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(final Date dtEnd) {
        this.dtEnd = dtEnd;
    }

    @Override
    public String toString() {
        return "Template{" +
            "id=" + id +
            ", calendarId=" + calendarId +
            ", eventTitle='" + eventTitle + '\'' +
            ", dtStart=" + dtStart +
            ", dtEnd=" + dtEnd +
            '}';
    }
}
