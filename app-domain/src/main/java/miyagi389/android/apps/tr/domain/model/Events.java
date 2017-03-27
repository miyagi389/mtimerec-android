package miyagi389.android.apps.tr.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Calendar Events
 */
public class Events implements Serializable {

    /**
     * id
     */
    private long id;

    /**
     * カレンダーID
     */
    private long calendarId;

    /**
     * タイトル
     */
    private String title;

    /**
     * 詳細
     */
    private String description;

    /**
     * イベント開始日時
     */
    private Date dtStart;

    /**
     * イベント終了日時
     */
    private Date dtEnd;

    /**
     * タイムゾーン
     */
    private String timezone;

    public Events() {
    }

    public Events(final Events copy) {
        this.id = copy.id;
        this.calendarId = copy.calendarId;
        this.title = copy.title;
        this.description = copy.description;
        this.dtStart = copy.dtStart;
        this.dtEnd = copy.dtEnd;
        this.timezone = copy.timezone;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "Events{" +
            "id=" + id +
            ", calendarId=" + calendarId +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", dtStart=" + dtStart +
            ", dtEnd=" + dtEnd +
            ", timezone='" + timezone + '\'' +
            '}';
    }
}
