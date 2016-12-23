package miyagi389.android.apps.tr.domain.model;

import java.io.Serializable;

/**
 * Calendar
 */
@SuppressWarnings("unused")
public class Calendars implements Serializable {

    /**
     * id
     */
    private long id;

    /**
     * カレンダー名
     */
    private String name;

    /**
     * アカウント名
     */
    private String accountName;

    /**
     * アカウント種別
     */
    private String accountType;

    /**
     * カレンダー色
     */
    private int calendarColor;

    /**
     * カレンダー表示名
     */
    private String calendarDisplayName;

    /**
     * カレンダーアクセスレベル
     */
    private int calendarAccessLevel;

    /**
     * 表示対象
     */
    private boolean visible;

    public Calendars() {
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public int getCalendarColor() {
        return calendarColor;
    }

    public void setCalendarColor(final int calendarColor) {
        this.calendarColor = calendarColor;
    }

    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public void setCalendarDisplayName(final String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
    }

    public int getCalendarAccessLevel() {
        return calendarAccessLevel;
    }

    public void setCalendarAccessLevel(final int calendarAccessLevel) {
        this.calendarAccessLevel = calendarAccessLevel;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "Calendars{" +
            "id=" + id +
            ", name=" + name +
            ", accountName='" + accountName + '\'' +
            ", accountType='" + accountType + '\'' +
            ", calendarColor='" + calendarColor + '\'' +
            ", calendarDisplayName='" + calendarDisplayName + '\'' +
            ", calendarAccessLevel=" + calendarAccessLevel +
            ", visible=" + visible +
            '}';
    }
}
