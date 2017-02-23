package miyagi389.android.apps.tr.data.provider.repository;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;

class TemplateSortOrder {

    static Map<TemplateRepository.SortOrder, Comparator<Template>> ITEMS = new HashMap<>();

    static {
        ITEMS.put(TemplateRepository.SortOrder.DT_START_ASCENDING, new DtStartAscending());
        ITEMS.put(TemplateRepository.SortOrder.DT_START_DESCENDING, new DtStartDescending());
        ITEMS.put(TemplateRepository.SortOrder.EVENT_TITLE_ASCENDING, new EventTitleAscending());
        ITEMS.put(TemplateRepository.SortOrder.EVENT_TITLE_DESCENDING, new EventTitleDescending());
    }

    static abstract class AbstractSortOrder implements Comparator<Template> {

        private final Collator eventTitleCollator = Collator.getInstance();

        int compareEventTitle(
            final Template lhs,
            final Template rhs,
            final int order
        ) {
            if (lhs.getEventTitle() == null && rhs.getEventTitle() == null) {
                return 0;
            }
            if (lhs.getEventTitle() != null && rhs.getEventTitle() == null) {
                return 1;
            }
            if (lhs.getEventTitle() == null && rhs.getEventTitle() != null) {
                return -1;
            }
            return eventTitleCollator.compare(lhs.getEventTitle(), rhs.getEventTitle()) * order;
        }


        int compareDtStart(
            final Template lhs,
            final Template rhs,
            final int order
        ) {
            if (lhs.getDtStart() == null && rhs.getDtStart() == null) {
                return -1;
            }
            if (lhs.getDtStart() != null && rhs.getDtStart() == null) {
                return -1;
            }
            if (lhs.getDtStart() == null && rhs.getDtStart() != null) {
                return 1;
            }
            return Long.compare(lhs.getDtStart().getTime(), rhs.getDtStart().getTime()) * order;
        }
    }

    private static class DtStartAscending extends AbstractSortOrder {

        @Override
        public int compare(
            final Template lhs,
            final Template rhs
        ) {
            int comp = compareDtStart(lhs, rhs, 1);
            if (comp != 0) {
                return comp;
            }
            comp = compareEventTitle(lhs, rhs, 1);
            if (comp != 0) {
                return comp;
            }
            return 0;
        }
    }

    private static class DtStartDescending extends AbstractSortOrder {

        @Override
        public int compare(
            final Template lhs,
            final Template rhs
        ) {
            int comp = compareDtStart(lhs, rhs, -1);
            if (comp != 0) {
                return comp;
            }
            comp = compareEventTitle(lhs, rhs, -1);
            if (comp != 0) {
                return comp;
            }
            return 0;
        }
    }

    private static class EventTitleAscending extends AbstractSortOrder {

        @Override
        public int compare(
            final Template lhs,
            final Template rhs
        ) {
            int comp = compareEventTitle(lhs, rhs, 1);
            if (comp != 0) {
                return comp;
            }
            comp = compareDtStart(lhs, rhs, 1);
            if (comp != 0) {
                return comp;
            }
            return 0;
        }
    }

    private static class EventTitleDescending extends AbstractSortOrder {

        @Override
        public int compare(
            final Template lhs,
            final Template rhs
        ) {
            int comp = compareEventTitle(lhs, rhs, -1);
            if (comp != 0) {
                return comp;
            }
            comp = compareDtStart(lhs, rhs, -1);
            if (comp != 0) {
                return comp;
            }
            return 0;
        }
    }
}
