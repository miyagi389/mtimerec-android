package miyagi389.android.apps.tr.domain.repository;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import miyagi389.android.apps.tr.domain.model.Template;

public interface TemplateRepository {

    enum SortOrder {
        DT_START_ASCENDING(1),
        DT_START_DESCENDING(2),
        EVENT_TITLE_ASCENDING(3),
        EVENT_TITLE_DESCENDING(4);

        public static final SortOrder DEFAULT = EVENT_TITLE_ASCENDING;

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

    Observable<Template> findAll(SortOrder sortOrder);

    @SuppressWarnings("unused")
    Maybe<Template> findById(long id);

    Maybe<Long> insert(Template model);

    Maybe<Long> update(Template model);

    Maybe<Long> deleteById(long id);

    Maybe<Long> startEvent(Template model);

    Maybe<Long> endEvent(Template model);
}
