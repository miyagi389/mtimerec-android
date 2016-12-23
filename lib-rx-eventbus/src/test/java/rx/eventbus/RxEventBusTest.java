package rx.eventbus;

import org.junit.Before;
import org.junit.Test;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RxEventBusTest {
    private RxEventBus bus;
    private RxEvent handledEvent;

    @Before
    public void setUp() {
        bus = new RxEventBus();
        handledEvent = null;
    }

    @Test
    public void post_unhandled_do_nothing() {
        RxEvent event = new UnhandledEvent();
        bus.post(event);
        assertEquals(0, event.handledCount);
    }

    @Test
    public void post_unhandled_call_unhandled_function() {
        RxEvent event = new UnhandledEvent();
        bus.post(event, new Action1<RxEvent>() {
            @Override
            public void call(RxEvent event) {
                handledEvent = event;
            }
        });
        assertEquals(handledEvent, event);
    }

    @Test
    public void post_unhandled_with_unsubscribe() {
        RxEvent event = new UnhandledEvent();
        Subscription subscription = bus.subscribe(UnhandledEvent.class, new Action1<UnhandledEvent>() {
            @Override
            public void call(UnhandledEvent event) {
                fail();
            }
        });
        subscription.unsubscribe();
        bus.post(event, new Action1<RxEvent>() {
            @Override
            public void call(RxEvent event) {
                handledEvent = event;
            }
        });
        assertEquals(handledEvent, event);
    }

    @Test
    public void post_unhandled_with_other_event() {
        RxEvent event = new UnhandledEvent();
        Subscription subscription = bus.subscribe(MyEvent.class, new Action1<MyEvent>() {
            @Override
            public void call(MyEvent event) {
                fail();
            }
        });
        bus.post(event, new Action1<RxEvent>() {
            @Override
            public void call(RxEvent event) {
                handledEvent = event;
            }
        });
        assertEquals(handledEvent, event);
    }

    @Test
    public void post_handled() {
        RxEvent event = new MyEvent(42);
        Subscription subscription = bus.subscribe(MyEvent.class, new Action1<MyEvent>() {
            @Override
            public void call(MyEvent event) {
                handledEvent = event;
            }
        });
        bus.post(event);
        subscription.unsubscribe();
        assertEquals(handledEvent, event);
    }

    @Test
    public void post_handled_2_times() {
        RxEvent event = new MyEvent(42);
        Action1<MyEvent> handler = new Action1<MyEvent>() {
            @Override
            public void call(MyEvent event) {
            }
        };
        Subscription subscription = new CompositeSubscription(bus.subscribe(MyEvent.class, handler), bus.subscribe(MyEvent.class, handler));
        bus.post(event);
        subscription.unsubscribe();
        assertEquals(2, event.handledCount);
    }

    @Test
    public void post_handled_new_thread() throws InterruptedException {
        final Thread mainThread = Thread.currentThread();
        final Object lock = new Object();
        Subscription subscription = bus.subscribe(MyEvent.class, new Action1<MyEvent>() {
            @Override
            public void call(MyEvent event) {
                assertNotEquals(mainThread, Thread.currentThread());
                handledEvent = event;
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }, Schedulers.newThread());
        bus.post(new MyEvent(42));
        synchronized (lock) {
            lock.wait(1000);
        }
        subscription.unsubscribe();
        assertNotNull(handledEvent);
    }

    static class MyEvent extends RxEvent {
        final int answer;

        MyEvent(int answer) {
            this.answer = answer;
        }
    }

    static class UnhandledEvent extends RxEvent {
    }
}
