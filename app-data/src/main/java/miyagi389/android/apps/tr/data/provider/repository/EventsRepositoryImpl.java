package miyagi389.android.apps.tr.data.provider.repository;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import miyagi389.android.apps.tr.data.exception.NotFoundException;
import miyagi389.android.apps.tr.data.provider.entity.EventsCountEntity;
import miyagi389.android.apps.tr.data.provider.entity.EventsEntity;
import miyagi389.android.apps.tr.data.provider.mapper.EventsCountMapper;
import miyagi389.android.apps.tr.data.provider.mapper.EventsMapper;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.model.EventsCount;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import rx.android.content.ContentObservable;

public class EventsRepositoryImpl implements EventsRepository {

    private final Context context;

    private final EventsMapper eventsMapper;
    private final EventsCountMapper eventsCountMapper;

    @Inject
    EventsRepositoryImpl(@NonNull final Context context) {
        this.context = context;
        this.eventsMapper = new EventsMapper();
        this.eventsCountMapper = new EventsCountMapper();
    }

    @Override
    public Observable<Events> findByCalendarIdLast(
        final long calendarId,
        final String title
    ) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperLastDtStart(context, calendarId, title))
            .map(cursor -> eventsMapper.transform(new EventsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Maybe<EventsCount> countByCalendarId(
        final long calendarId,
        final String title
    ) {
        return ContentObservable.fromCursor(EventsCountEntity.Utils.toCursorWrapperCalendarId(context, calendarId, title))
            .map(cursor -> eventsCountMapper.transform(new EventsCountEntity.CursorWrapper(cursor)))
            .firstElement();
    }

    @Override
    public Observable<Events> findByCalendarId(
        final long calendarId,
        final String title,
        final long fromDate,
        final long toDate,
        final SortOrder sortOrder
    ) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperCalendarId(context, calendarId, title, fromDate, toDate, sortOrder))
            .map(cursor -> eventsMapper.transform(new EventsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Maybe<Events> findById(final long id) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperById(context, id))
            .map(cursor -> eventsMapper.transform(new EventsEntity.CursorWrapper(cursor)))
            .firstElement();
    }

    @Override
    public Maybe<Long> insert(final Events model) {
        if (model == null) {
            return Maybe.error(new Exception("events is null."));
        }

        final EventsEntity entity = eventsMapper.transform(model);
        if (entity == null) {
            return Maybe.error(new Exception("events entity is null."));
        }

        return Maybe.create(e -> {
            if (e.isDisposed()) {
                return;
            }

            final Uri uri = EventsEntity.Utils.insert(context, entity);

            if (uri == null) {
                e.onError(new Exception("insert error."));
            } else {
                e.onSuccess(ContentUris.parseId(uri));
            }
        });
    }

    @Override
    public Maybe<Long> update(final Events model) {
        if (model == null) {
            return Maybe.error(new Exception("events is null."));
        }

        final EventsEntity existEntity = toEventsEntity(model);
        if (existEntity == null) {
            return Maybe.error(new NotFoundException("events not found on database. id=" + model.getId()));
        }

        final EventsEntity entity = eventsMapper.transform(model);

        return Maybe.create(e -> {
            if (e.isDisposed()) {
                return;
            }

            if (entity == null) {
                e.onError(new Exception("model transform error. model is null."));
                return;
            }

            final int numberOfRowsUpdated = EventsEntity.Utils.update(context, entity);

            if (numberOfRowsUpdated == 0) {
                e.onError(new Exception("update error. numberOfRowsUpdated=" + numberOfRowsUpdated));
            } else {
                e.onSuccess(entity.id);
            }
        });
    }

    @Override
    public Maybe<Long> deleteById(final long id) {
        return Maybe.create(e -> {
            if (e.isDisposed()) {
                return;
            }

            final int numberOfRowsDeleted = EventsEntity.Utils.deleteById(context, id);

            if (numberOfRowsDeleted == 0) {
                e.onError(new Exception("delete error. numberOfRowsDeleted=" + numberOfRowsDeleted));
            } else {
                e.onSuccess(id);
            }
        });
    }

    @Nullable
    private EventsEntity toEventsEntity(@Nullable final Events model) {
        if (model == null) {
            return null;
        }
        return toEventsEntity(model.getId());
    }

    @Nullable
    private EventsEntity toEventsEntity(final long id) {
        try (final EventsEntity.CursorWrapper c = EventsEntity.Utils.toCursorWrapperById(context, id)) {
            if (c != null && c.moveToFirst()) {
                return c.toEntity();
            }
        }
        return null;
    }
}
