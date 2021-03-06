package miyagi389.android.apps.tr.data.provider.repository;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import miyagi389.android.apps.tr.data.exception.NotFoundException;
import miyagi389.android.apps.tr.data.provider.entity.EventsEntity;
import miyagi389.android.apps.tr.data.provider.entity.TemplateEntity;
import miyagi389.android.apps.tr.data.provider.mapper.TemplateMapper;
import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.domain.model.Template;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import rx.android.content.ContentObservable;
import rx.eventbus.RxEventBus;

public class TemplateRepositoryImpl implements TemplateRepository {

    private final Context context;

    private final RxEventBus eventBus;

    private final EventsRepository eventsRepository;

    private final TemplateMapper mapper;

    @Inject
    TemplateRepositoryImpl(
        @NonNull final Context context,
        @NonNull final RxEventBus eventBus,
        final EventsRepository eventsRepository
    ) {
        this.context = context;
        this.eventBus = eventBus;
        this.eventsRepository = eventsRepository;
        this.mapper = new TemplateMapper();
    }

    @Override
    public Observable<Template> findAll(final SortOrder sortOrder) {
        return ContentObservable.fromCursor(TemplateEntity.Utils.toCursorWrapperByAll(context))
            .map(cursor -> mapper.transform(new TemplateEntity.CursorWrapper(cursor)))
            .map(entity -> {
                try (final EventsEntity.CursorWrapper c = EventsEntity.Utils.toCursorWrapperLastDtStart(context, entity.getCalendarId(), entity.getEventTitle())) {
                    if (c != null && c.moveToNext()) {
                        entity.setDtStart(new Date(c.getDtStart()));
                        entity.setDtEnd(new Date(c.getDtEnd()));
                    }
                }
                return entity;
            })
            .toSortedList(TemplateSortOrder.ITEMS.get(sortOrder))
            .flattenAsObservable(entities -> entities);
    }

    @Override
    public Maybe<Template> findById(final long id) {
        return ContentObservable.fromCursor(TemplateEntity.Utils.toCursorWrapperById(context, id))
            .map(cursor -> mapper.transform(new TemplateEntity.CursorWrapper(cursor)))
            .map(entity -> {
                try (final EventsEntity.CursorWrapper c = EventsEntity.Utils.toCursorWrapperLastDtStart(context, entity.getCalendarId(), entity.getEventTitle())) {
                    if (c != null && c.moveToNext()) {
                        entity.setDtStart(new Date(c.getDtStart()));
                        entity.setDtEnd(new Date(c.getDtEnd()));
                    }
                }
                return entity;
            })
            .firstElement();
    }

    @Override
    public Maybe<Long> insert(final Template model) {
        if (model == null) {
            return Maybe.error(new Exception("template is null."));
        }

        final TemplateEntity entity = mapper.transform(model);
        if (entity == null) {
            return Maybe.error(new Exception("template entity is null."));
        }

        return Maybe.create(e -> {
            if (e.isDisposed()) {
                return;
            }

            final Uri uri = TemplateEntity.Utils.insert(context, entity);

            if (uri == null) {
                e.onError(new Exception("insert error."));
            } else {
                eventBus.post(new Template.Created(ContentUris.parseId(uri)));
                e.onSuccess(ContentUris.parseId(uri));
            }
        });
    }

    @Override
    public Maybe<Long> update(final Template model) {
        if (model == null) {
            return Maybe.error(new Exception("template is null."));
        }

        final TemplateEntity existEntity = toTemplateEntity(model);
        if (existEntity == null) {
            return Maybe.error(new NotFoundException("template not found on database. id=" + model.getId()));
        }

        final TemplateEntity entity = mapper.transform(model);
        if (entity == null) {
            return Maybe.error(new Exception("template entity is null."));
        }

        return Maybe.create(e -> {
            if (e.isDisposed()) {
                return;
            }

            final int numberOfRowsUpdated = TemplateEntity.Utils.update(context, entity);

            if (numberOfRowsUpdated == 0) {
                e.onError(new Exception("update error. numberOfRowsUpdated=" + numberOfRowsUpdated));
            } else {
                eventBus.post(new Template.Updated(model.getId()));
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

            final int numberOfRowsDeleted = TemplateEntity.Utils.deleteById(context, id);

            if (numberOfRowsDeleted == 0) {
                e.onError(new Exception("delete error. numberOfRowsDeleted=" + numberOfRowsDeleted));
            } else {
                eventBus.post(new Template.Deleted(id));
                e.onSuccess(id);
            }
        });
    }

    @Override
    public Maybe<Long> startEvent(final Template model) {
        if (model == null) {
            return Maybe.error(new Exception("template is null."));
        }

        final Date now = nowDate();

        final Events events = new Events();
        events.setCalendarId(model.getCalendarId());
        events.setTitle(model.getEventTitle());
        events.setTimezone(TimeZone.getDefault().getID());
        events.setDtEnd(now);
        events.setDtStart(now);

        return eventsRepository.insert(events);
    }

    @Override
    public Maybe<Long> endEvent(final Template model) {
        if (model == null) {
            return Maybe.error(new Exception("template is null."));
        }

        final List<Events> eventsList = eventsRepository.findByCalendarIdLast(model.getCalendarId(), model.getEventTitle()).toList().blockingGet();
        final Date now = nowDate();
        final Events events;
        if (eventsList.size() == 0) {
            events = new Events();
            events.setCalendarId(model.getCalendarId());
            events.setTitle(model.getEventTitle());
            events.setTimezone(TimeZone.getDefault().getID());
            events.setDtEnd(now);
            events.setDtStart(now);
            return eventsRepository.insert(events);
        } else {
            events = eventsList.get(0);
            events.setDtEnd(now);
            return eventsRepository.update(events);
        }
    }

    @NonNull
    private Date nowDate() {
        return new Date();
    }

    @Nullable
    private TemplateEntity toTemplateEntity(@Nullable final Template model) {
        if (model == null) {
            return null;
        }
        return toTemplateEntity(model.getId());
    }

    @Nullable
    private TemplateEntity toTemplateEntity(final long id) {
        try (final TemplateEntity.CursorWrapper c = TemplateEntity.Utils.toCursorWrapperById(context, id)) {
            if (c != null && c.moveToFirst()) {
                return c.toEntity();
            }
        }
        return null;
    }
}
