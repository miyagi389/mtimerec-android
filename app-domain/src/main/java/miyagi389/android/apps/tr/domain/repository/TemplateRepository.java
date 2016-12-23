package miyagi389.android.apps.tr.domain.repository;

import miyagi389.android.apps.tr.domain.model.Template;
import rx.Observable;
import rx.Single;

public interface TemplateRepository {

    Observable<Template> findAll();

    @SuppressWarnings("unused")
    Single<Template> findById(long id);

    Single<Long> insert(Template model);

    Single<Long> update(Template model);

    Single<Long> deleteById(long id);

    Single<Long> startEvent(Template model);

    Single<Long> endEvent(Template model);
}
