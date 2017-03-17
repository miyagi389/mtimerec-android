package miyagi389.android.apps.tr.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Calendar Events count
 */
public class EventsCount implements Serializable {

    /**
     * id
     */
    private int count;

    /**
     * イベント開始日時 - 最小
     */
    private Date minDtStart;

    /**
     * イベント終了日時 - 最大
     */
    private Date maxDtEnd;

    public EventsCount() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public Date getMinDtStart() {
        return minDtStart;
    }

    public void setMinDtStart(final Date minDtStart) {
        this.minDtStart = minDtStart;
    }

    public Date getMaxDtEnd() {
        return maxDtEnd;
    }

    public void setMaxDtEnd(final Date maxDtEnd) {
        this.maxDtEnd = maxDtEnd;
    }

    @Override
    public String toString() {
        return "EventsCount{" +
            "count=" + count +
            ", minDtStart=" + minDtStart +
            ", maxDtEnd=" + maxDtEnd +
            '}';
    }
}
