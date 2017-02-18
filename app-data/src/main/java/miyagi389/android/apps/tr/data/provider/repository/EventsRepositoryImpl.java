package miyagi389.android.apps.tr.data.provider.repository;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import miyagi389.android.apps.tr.data.exception.NotFoundException;
import miyagi389.android.apps.tr.data.provider.entity.EventsEntity;
import miyagi389.android.apps.tr.data.provider.mapper.EventsMapper;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
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
            .asObservable()
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Observable<Events> findByCalendarId(
        final long calendarId,
        final String title,
        final SortOrder sortOrder
    ) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperCalendarId(context, calendarId, title, sortOrder))
            .asObservable()
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
            .asObservable()
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)));
    }

    @Override
    public Single<Events> findById(final long id) {
        return ContentObservable.fromCursor(EventsEntity.Utils.toCursorWrapperById(context, id))
            .asObservable()
            .map(cursor -> mapper.transform(new EventsEntity.CursorWrapper(cursor)))
            .toSingle();
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

        return Single.create(new Single.OnSubscribe<Long>() {
            @Override
            public void call(final SingleSubscriber<? super Long> singleSubscriber) {
                if (singleSubscriber.isUnsubscribed()) {
                    return;
                }

                final Uri uri = EventsEntity.Utils.insert(context, entity);

                if (uri == null) {
                    singleSubscriber.onError(new Exception("insert error."));
                } else {
                    singleSubscriber.onSuccess(ContentUris.parseId(uri));
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

        return Single.create(new Single.OnSubscribe<Long>() {
            @Override
            public void call(final SingleSubscriber<? super Long> singleSubscriber) {
                if (singleSubscriber.isUnsubscribed()) {
                    return;
                }

                if (entity == null) {
                    singleSubscriber.onError(new Exception("model transform error. model is null."));
                    return;
                }

                final int numberOfRowsUpdated = EventsEntity.Utils.update(context, entity);

                if (numberOfRowsUpdated == 0) {
                    singleSubscriber.onError(new Exception("update error. numberOfRowsUpdated=" + numberOfRowsUpdated));
                } else {
                    singleSubscriber.onSuccess(entity.id);
                }
            }
        });
    }

    @Override
    public Single<Long> deleteById(final long id) {
        return Single.create(new Single.OnSubscribe<Long>() {
            @Override
            public void call(final SingleSubscriber<? super Long> singleSubscriber) {
                if (singleSubscriber.isUnsubscribed()) {
                    return;
                }

                final int numberOfRowsDeleted = EventsEntity.Utils.deleteById(context, id);

                if (numberOfRowsDeleted == 0) {
                    singleSubscriber.onError(new Exception("delete error. numberOfRowsDeleted=" + numberOfRowsDeleted));
                } else {
                    singleSubscriber.onSuccess(id);
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
