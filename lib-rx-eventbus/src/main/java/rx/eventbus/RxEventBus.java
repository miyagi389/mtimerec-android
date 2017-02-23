package rx.eventbus;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * <p>An implementation of event bus using {@link PublishSubject}.</p>
 * <p>MT-Safe.</p>
 * <p>Not support generics.</p>
 */
public class RxEventBus {

    public RxEventBus() {
    }

    private final PublishSubject<RxEvent> bus = PublishSubject.create();

    public <E extends RxEvent> void post(final E o) {
        bus.onNext(o);
    }

    public <E extends RxEvent> Observable<E> toObservable(final Class<E> clazz) {
        return bus
            .ofType(clazz)
            .doOnNext(new Consumer<E>() {
                @Override
                public void accept(@NonNull final E e) throws Exception {
                    e.handledCount++;
                }
            });
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
