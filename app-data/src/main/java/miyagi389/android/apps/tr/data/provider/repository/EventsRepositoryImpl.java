package miyagi389.android.apps.tr.data.provider.repository;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import miyagi389.android.apps.tr.data.exception.NotFoundException;
import miyagi389.android.apps.tr.data.provider.entity.EventsEntity;
import miyagi389.android.apps.tr.data.provider.mapper.EventsMapper;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import rx.android.content.ContentObservable;

public class EventsRepositoryImpl implements EventsRepository {

    private final Context context;

    private final EventsMapper mapper;

    @Inject
    EventsRepositoryImpl(@NonNull final Context context) {
        this.context = context;
        this.mapper = new EventsMapper();
    }

    @Override
    public Observable<Events> findByCalendarIdLast(
        final long calendarId,
        final String title
    ) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperLastDtStart(context, calendarId, title))
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Observable<Events> findByCalendarId(
        final long calendarId,
        final String title,
        final SortOrder sortOrder
    ) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperCalendarId(context, calendarId, title, sortOrder))
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)));
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
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Single<Events> findById(final long id) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperById(context, id))
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)))
            .firstOrError();
    }

    @Override
    public Single<Long> insert(final Events model) {
        if (model == null) {
            return Single.error(new Exception("events is null."));
        }

        final EventsEntity entity = mapper.transform(model);
        if (entity == null) {
            return Single.error(new Exception("events entity is null."));
        }

        return Single.create(new SingleOnSubscribe<Long>() {
            @Override
            public void subscribe(final SingleEmitter<Long> e) throws Exception {
                if (e.isDisposed()) {
                    return;
                }

                final Uri uri = EventsEntity.Utils.insert(context, entity);

                if (uri == null) {
                    e.onError(new Exception("insert error."));
                } else {
                    e.onSuccess(ContentUris.parseId(uri));
                }
            }
        });
    }

    @Override
    public Single<Long> update(final Events model) {
        if (model == null) {
            return Single.error(new Exception("events is null."));
        }

        final EventsEntity existEntity = toEventsEntity(model);
        if (existEntity == null) {
            return Single.error(new NotFoundException("events not found on database. id=" + model.getId()));
        }

        final EventsEntity entity = mapper.transform(model);

        return Single.create(new SingleOnSubscribe<Long>() {
            @Override
            public void subscribe(final SingleEmitter<Long> e) throws Exception {
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
            }
        });
    }

    @Override
    public Single<Long> deleteById(final long id) {
        return Single.create(new SingleOnSubscribe<Long>() {
            @Override
            public void subscribe(final SingleEmitter<Long> e) throws Exception {
                if (e.isDisposed()) {
                    return;
                }

                final int numberOfRowsDeleted = EventsEntity.Utils.deleteById(context, id);

                if (numberOfRowsDeleted == 0) {
                    e.onError(new Exception("delete error. numberOfRowsDeleted=" + numberOfRowsDeleted));
                } else {
                    e.onSuccess(id);
                }
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
