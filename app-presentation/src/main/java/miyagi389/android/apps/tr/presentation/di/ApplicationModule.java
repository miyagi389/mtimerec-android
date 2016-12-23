package miyagi389.android.apps.tr.presentation.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import miyagi389.android.apps.tr.data.provider.repository.CalendarsRepositoryImpl;
import miyagi389.android.apps.tr.data.provider.repository.EventsRepositoryImpl;
import miyagi389.android.apps.tr.data.provider.repository.TemplateRepositoryImpl;
import miyagi389.android.apps.tr.domain.repository.CalendarsRepository;
import miyagi389.android.apps.tr.domain.repository.EventsRepository;
import miyagi389.android.apps.tr.domain.repository.TemplateRepository;
import rx.eventbus.RxEventBus;

@Module
public class ApplicationModule {

    private final ApplicationModule self = this;

    private final Context context;

    public ApplicationModule(@NonNull final Application application) {
        this.context = application;
    }

    @Provides
    public Context provideContext() {
        return self.context;
    }

    @Provides
    @Singleton
    public RxEventBus provideRxEventBus() {
        return new RxEventBus();
    }

    @Provides
    @Singleton
    CalendarsRepository provideCalendarsRepository(final CalendarsRepositoryImpl calendarsRepository) {
        return calendarsRepository;
    }

    @Provides
    @Singleton
    EventsRepository provideEventsRepository(final EventsRepositoryImpl eventsRepository) {
        return eventsRepository;
    }

    @Provides
    @Singleton
    TemplateRepository provideTemplateRepository(final TemplateRepositoryImpl templateRepository) {
        return templateRepository;
    }
}
